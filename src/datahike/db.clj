(ns datahike.db)

(defmacro raise [& fragments]
  (let [msgs (butlast fragments)
        data (last fragments)]
    `(throw (ex-info (str ~@(map (fn [m#] (if (string? m#) m# (list 'pr-str m#))) msgs)) ~data))))

(defn- cljs-env?
  "Take the &env from a macro, and tell whether we are expanding into cljs."
  [env]
  (boolean (:ns env)))

(defmacro if-cljs
  "Return then if we are generating cljs code and else for Clojure code.
  https://groups.google.com/d/msg/clojurescript/iBY5HaQda4A/w1lAQi9_AwsJ"
  [then else]
  (if (cljs-env? &env) then else))

(defn- get-sig [method]
  ;; expects something like '(method-symbol [arg arg arg] ...)
  ;; if the thing matches, returns [fully-qualified-symbol arity], otherwise nil
  (and (sequential? method)
      (symbol? (first method))
      (vector? (second method))
      (let [sym (first method)
            ns (or (some->> sym resolve meta :ns str) "clojure.core")]
        [(symbol ns (name sym)) (-> method second count)])))

(defn- dedupe-interfaces [deftype-form]
  ;; get the interfaces list, remove any duplicates, similar to remove-nil-implements in potemkin
  ;; verified w/ deftype impl in compiler:
  ;; (deftype* tagname classname [fields] :implements [interfaces] :tag tagname methods*)
  (let [[deftype* tagname classname fields implements interfaces & rest] deftype-form]
    (when (or (not= deftype* 'deftype*) (not= implements :implements))
      (throw (IllegalArgumentException. "deftype-form mismatch")))
    (list* deftype* tagname classname fields implements (vec (distinct interfaces)) rest)))

(defn- make-record-updatable-clj [name fields & impls]
  (let [impl-map (->> impls (map (juxt get-sig identity)) (filter first) (into {}))
        body (macroexpand-1 (list* 'defrecord name fields impls))]
    (clojure.walk/postwalk
      (fn [form]
        (if (and (sequential? form) (= 'deftype* (first form)))
          (->> form
              dedupe-interfaces
              (remove (fn [method]
                        (when-let [impl (-> method get-sig impl-map)]
                          (not= method impl)))))
          form))
      body)))

(defn- make-record-updatable-cljs [name fields & impls]
  `(do
     (defrecord ~name ~fields)
     (extend-type ~name ~@impls)))

(defmacro defrecord-updatable [name fields & impls]
  `(if-cljs
    ~(apply make-record-updatable-cljs name fields impls)
    ~(apply make-record-updatable-clj name fields impls)))

(defmacro cond+ [& clauses]
  (when-some [[test expr & rest] clauses]
    (case test
      :let `(let ~expr (cond+ ~@rest))
      `(if ~test ~expr (cond+ ~@rest)))))

(defmacro some-of
  ([] nil)
  ([x] x)
  ([x & more]
  `(let [x# ~x] (if (nil? x#) (some-of ~@more) x#))))