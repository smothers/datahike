(ns datahike.schema
  (:require [clojure.spec.alpha :as s])
  #?(:clj (:import [datahike.datom Datom])))

(s/def :db.type/id #(or string?
                        #?(:clj (= (class %) java.lang.Long)
                           :cljs integer?)))

;; db types
#?(:clj (s/def :db.type/bigdec decimal?))
(s/def :db.type/bigint integer?)
(s/def :db.type/boolean boolean?)
(s/def :db.type/double double?)
(s/def :db.type/float float?)
(s/def :db.type/number number?)
(s/def :db.type/instant inst?)
(s/def :db.type/keyword keyword?)
#?(:clj (s/def :db.type/long #(= (class %) java.lang.Long)))
(s/def :db.type/ref :db.type/id)
(s/def :db.type/string string?)
(s/def :db.type/symbol symbol?)
(s/def :db.type/uuid uuid?)

(s/def :db.type/value
  #{:db.type/bigdec
    :db.type/bigint
    :db.type/boolean
    :db.type/double
    :db.type/float
    :db.type/number
    :db.type/instant
    :db.type/keyword
    :db.type/long
    :db.type/ref
    :db.type/string
    :db.type/symbol
    :db.type/uuid
    :db.type/value})

;; TODO: add tuples, bytes

(s/def :db.type/cardinality #{:db.cardinality/one :db.cardinality/many})
(s/def :db.type/unique #{:db.unique/identity :db.unique/value})

;; only for old datomic compliance, will be part of partioning in the future
(s/def :db.type.install/_attribute #{:db.part/tx :db.part/db :db.part/user})

(s/def ::schema-attribute #{:db/id :db/ident :db/isComponent :db/valueType :db/cardinality :db/unique :db/index :db.install/_attribute :db/doc})
(s/def ::meta-attribute #{:db/txInstant :db/retracted})

(s/def ::schema (s/keys :req [:db/ident :db/valueType :db/cardinality]
                        :opt [:db/id :db/unique :db/index :db.install/_attribute :db/doc]))

(def required-keys #{:db/ident :db/valueType :db/cardinality})

(def ^:const implicit-schema-spec {:db/ident {:db/valueType   :db.type/keyword
                                              :db/unique      :db.unique/identity
                                              :db/cardinality :db.cardinality/one}
                                   :db/valueType {:db/valueType   :db.type/value
                                                  :db/unique      :db.unique/identity
                                                  :db/cardinality :db.cardinality/one}
                                   :db/id {:db/valueType   :db.type/id
                                           :db/unique      :db.unique/identity
                                           :db/cardinality :db.cardinality/one}
                                   :db/cardinality {:db/valueType   :db.type/cardinality
                                                    :db/unique      :db.unique/identity
                                                    :db/cardinality :db.cardinality/one}
                                   :db/index {:db/valueType   :db.type/boolean
                                              :db/unique      :db.unique/identity
                                              :db/cardinality :db.cardinality/one}
                                   :db/unique {:db/valueType   :db.type/unique
                                               :db/unique      :db.unique/identity
                                               :db/cardinality :db.cardinality/one}
                                   :db/isComponent {:db/valueType :db.type/boolean
                                                    :db/unique :db.unique/identity
                                                    :db/cardinality :db.cardinality/one}
                                   :db/txInstant {:db/valueType :db.type/instant
                                                  :db/unique :db.unique/identity
                                                  :db/index true
                                                  :db/cardinality :db.cardinality/one}
                                   :db/retracted {:db/valueType :db.type/long
                                                  :db/unique :db.unique/identity
                                                  :db/cardinality :db.cardinality/many}
                                   :db/doc {:db/valueType :db.type/string
                                            :db/index true
                                            :db/cardinality :db.cardinality/one}
                                   :db.install/_attribute {:db/valueType   :db.type.install/_attribute
                                                           :db/unique      :db.unique/identity
                                                           :db/cardinality :db.cardinality/one}})

(def schema-keys #{:db/ident :db/isComponent :db/valueType :db/cardinality :db/unique :db/index :db.install/_attribute :db/doc})

(defn meta-attr? [attr]
  (s/valid? ::meta-attribute attr))

(defn schema-attr? [attr]
  (s/valid? ::schema-attribute attr))

(defn value-valid? [[_ _ a v _] schema]
  (let [schema (if (or (meta-attr? a) (schema-attr? a))
                 implicit-schema-spec
                 schema)
        value-type (get-in schema [a :db/valueType])]
    (s/valid? value-type v)))

(defn instant? [^Datom datom schema]
  (let [a (.-a datom)
        schema (if (or (meta-attr? a) (schema-attr? a))
                 implicit-schema-spec
                 schema)]
    (= (get-in schema [a :db/valueType]) :db.type/instant)))

(defn schema-entity? [entity]
  (some #(contains? entity %) schema-keys))

(defn schema? [schema]
  (s/valid? ::schema schema))

(defn describe-type [schema-type]
  (s/describe schema-type))

(defn find-invalid-schema-updates [entity attr-schema]
  (reduce-kv
   (fn [m attr-def new-value]
     (let [old-value (get-in attr-schema [attr-def])]
       (when-not (= old-value new-value)
         (case attr-def
           :db/cardinality (if (= new-value :db.cardinality/many)
                             (if (get-in attr-schema [:db/unique])
                               (assoc m attr-def [old-value new-value])
                               nil)
                             (assoc m attr-def [old-value new-value]))
           :db/unique (when-not (get-in attr-schema [:db/unique])
                        (when-not (= (get-in attr-schema [:db/cardinality]) :db.cardinality/one)
                          (assoc m attr-def [old-value new-value])))
           (assoc m attr-def [old-value new-value])))))
   {}
   (dissoc entity :db/id)))
