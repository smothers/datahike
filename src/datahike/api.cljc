(ns datahike.api
  (:refer-clojure :exclude [filter])
  (:require [datahike.connector :as dc]
            [datahike.pull-api :as dp]
            [datahike.query :as dq]
            [datahike.db :as db #?@(:cljs [:refer [HistoricalDB AsOfDB SinceDB FilteredDB]])]
            [datahike.impl.entity :as de #?@(:cljs [:refer [Entity]])])
  #?(:clj
     (:import [datahike.db HistoricalDB AsOfDB SinceDB FilteredDB]
              [datahike.impl.entity Entity]
              [java.util Date])))

(def
  ^{:arglists '([uri])
    :doc
              "Connects to a datahike database via URI or configuration. URI contains storage backend type
            and additional information for backends like database name, credentials, or
            location. Refer to the store project in the examples folder or the documentation
            in the config markdown file in the doc folder.

            Usage:

              (connect \"datahike:mem://example\")"}
  connect dc/connect)

(def
  ^{:arglists '([config])
    :doc "Checks if a database exists given a configuration URI or hash map.
          Usage:

            (database-exists? \"datahike:mem://example\")"}
  database-exists? dc/database-exists?)

(def
  ^{:arglists '([config & opts])
    :doc
              "Creates a database using backend configuration with optional database configuration
            by providing either a URI that encodes storage backend data like database name,
            credentials, or location, or by providing a configuration hash map. Refer to the
            store project in the examples folder or the documention in the config markdown
            file of the doc folder.

            Usage:

              Create an empty database with default configuration:

                `(create-database \"datahike:mem://example\")`

              Initial data after creation may be added using the `:initial-tx` parameter, which in this example adds a schema:

                (create-database \"datahike:mem://example\" :initial-tx [{:db/ident :name :db/valueType :db.type/string :db.cardinality/one}])

              Datahike has a strict schema validation (schema-on-write) policy by default,
              that only allows transaction of data that that has been pre-defined by a schema.
              You may influence this behaviour using the `:schema-on-read` parameter:

                (create-database \"datahike:mem://example\" :schema-on-read true)

              By storing historical data in a separate index, datahike has the capability of
              querying data from any point in time. You may control this feature using the
              `:temporal-index` parameter:

                (create-database \"datahike:mem://example\" :temporal-index false)"}
  create-database
  dc/create-database)

(def ^{:arglists '([uri])
       :doc      "Deletes a database at given URI."}
  delete-database
  dc/delete-database)

(def ^{:arglists '([conn tx-data])
       :doc      "Applies transaction the underlying database value and atomically updates connection reference to point to the result of that transaction, new db value.
  Returns transaction report, a map:

       { :db-before ...       ; db value before transaction
         :db-after  ...       ; db value after transaction
         :tx-data   [...]     ; plain datoms that were added/retracted from db-before
         :tempids   {...}     ; map of tempid from tx-data => assigned entid in db-after
         :tx-meta   tx-meta } ; the exact value you passed as `tx-meta`

  Note! `conn` will be updated in-place and is not returned from [[transact]].
  
  Usage:

      ; add a single datom to an existing entity (1)
      (transact conn [[:db/add 1 :name \"Ivan\"]])
  
      ; retract a single datom
      (transact conn [[:db/retract 1 :name \"Ivan\"]])
  
      ; retract single entity attribute
      (transact conn [[:db.fn/retractAttribute 1 :name]])
      
      ; retract all entity attributes (effectively deletes entity)
      (transact conn [[:db.fn/retractEntity 1]])
  
      ; create a new entity (`-1`, as any other negative value, is a tempid
      ; that will be replaced with DataScript to a next unused eid)
      (transact conn [[:db/add -1 :name \"Ivan\"]])
  
      ; check assigned id (here `*1` is a result returned from previous `transact` call)
      (def report *1)
      (:tempids report) ; => {-1 296}
  
      ; check actual datoms inserted
      (:tx-data report) ; => [#datahike/Datom [296 :name \"Ivan\"]]
  
      ; tempid can also be a string
      (transact conn [[:db/add \"ivan\" :name \"Ivan\"]])
      (:tempids *1) ; => {\"ivan\" 297}
  
      ; reference another entity (must exist)
      (transact conn [[:db/add -1 :friend 296]])
  
      ; create an entity and set multiple attributes (in a single transaction
      ; equal tempids will be replaced with the same unused yet entid)
      (transact conn [[:db/add -1 :name \"Ivan\"]
                       [:db/add -1 :likes \"fries\"]
                       [:db/add -1 :likes \"pizza\"]
                       [:db/add -1 :friend 296]])
  
      ; create an entity and set multiple attributes (alternative map form)
      (transact conn [{:db/id  -1
                        :name   \"Ivan\"
                        :likes  [\"fries\" \"pizza\"]
                        :friend 296}])
      
      ; update an entity (alternative map form). Can’t retract attributes in
      ; map form. For cardinality many attrs, value (fish in this example)
      ; will be added to the list of existing values
      (transact conn [{:db/id  296
                        :name   \"Oleg\"
                        :likes  [\"fish\"]}])

      ; ref attributes can be specified as nested map, that will create netsed entity as well
      (transact conn [{:db/id  -1
                        :name   \"Oleg\"
                        :friend {:db/id -2
                                 :name \"Sergey\"}])
                                 
      ; reverse attribute name can be used if you want created entity to become
      ; a value in another entity reference
      (transact conn [{:db/id  -1
                        :name   \"Oleg\"
                        :_friend 296}])
      ; equivalent to
      (transact conn [{:db/id  -1, :name   \"Oleg\"}
                       {:db/id 296, :friend -1}])
      ; equivalent to
      (transact conn [[:db/add  -1 :name   \"Oleg\"]
                       {:db/add 296 :friend -1]])"}
  transact
  dc/transact)

(def ^{:arglists '([conn tx-data tx-meta])
       :doc      "The same as [[transact]] but returns Future to be realized."}
  transact!
  dc/transact!)

(def ^{:arglists '([conn])
       :doc      "Releases a database connection"}
  release dc/release)

(def ^{:arglists '([db selector eid])
       :doc      "Fetches data from database using recursive declarative description. See [docs.datomic.com/on-prem/pull.html](https://docs.datomic.com/on-prem/pull.html).

             Unlike [[entity]], returns plain Clojure map (not lazy).

             Usage:

                 (pull db [:db/id, :name, :likes, {:friends [:db/id :name]}] 1)
                 ; => {:db/id   1,
                 ;     :name    \"Ivan\"
                 ;     :likes   [:pizza]
                 ;     :friends [{:db/id 2, :name \"Oleg\"}]"}
  pull dp/pull)

(def ^{:arglists '([db selector eids])
       :doc      "Same as [[pull]], but accepts sequence of ids and returns sequence of maps.

             Usage:

             ```
             (pull-many db [:db/id :name] [1 2])
             ; => [{:db/id 1, :name \"Ivan\"}
             ;     {:db/id 2, :name \"Oleg\"}]
             ```"}
  pull-many dp/pull-many)


(defmulti q
  "Executes a datalog query. See [docs.datomic.com/on-prem/query.html](https://docs.datomic.com/on-prem/query.html).

   Usage:

   ```
   (q '[:find ?value
        :where [_ :likes ?value]]
      db)
   ; => #{[\"fries\"] [\"candy\"] [\"pie\"] [\"pizza\"]}
   ```"
  {:arglists '([query & inputs])}
  (fn [query & inputs] (type query)))

(defmethod q clojure.lang.PersistentVector
  [query & inputs]
  (apply dq/q query inputs))

(defmethod q clojure.lang.PersistentArrayMap
  [query-map & arg-list]
  (let [query (if (contains? query-map :query)
                (:query query-map)
                query-map)
        args (if (contains? query-map :args)
               (:args query-map)
               arg-list)]
    (apply dq/q query args)))
(defn datoms
  "Index lookup. Returns a sequence of datoms (lazy iterator over actual DB index) which components (e, a, v) match passed arguments.

   Datoms are sorted in index sort order. Possible `index` values are: `:eavt`, `:aevt`, `:avet`.

   Usage:

       ; find all datoms for entity id == 1 (any attrs and values)
       ; sort by attribute, then value
       (datoms db :eavt 1)
       ; => (#datahike/Datom [1 :friends 2]
       ;     #datahike/Datom [1 :likes \"fries\"]
       ;     #datahike/Datom [1 :likes \"pizza\"]
       ;     #datahike/Datom [1 :name \"Ivan\"])

       ; find all datoms for entity id == 1 and attribute == :likes (any values)
       ; sorted by value
       (datoms db :eavt 1 :likes)
       ; => (#datahike/Datom [1 :likes \"fries\"]
       ;     #datahike/Datom [1 :likes \"pizza\"])

       ; find all datoms for entity id == 1, attribute == :likes and value == \"pizza\"
       (datoms db :eavt 1 :likes \"pizza\")
       ; => (#datahike/Datom [1 :likes \"pizza\"])

       ; find all datoms for attribute == :likes (any entity ids and values)
       ; sorted by entity id, then value
       (datoms db :aevt :likes)
       ; => (#datahike/Datom [1 :likes \"fries\"]
       ;     #datahike/Datom [1 :likes \"pizza\"]
       ;     #datahike/Datom [2 :likes \"candy\"]
       ;     #datahike/Datom [2 :likes \"pie\"]
       ;     #datahike/Datom [2 :likes \"pizza\"])

       ; find all datoms that have attribute == `:likes` and value == `\"pizza\"` (any entity id)
       ; `:likes` must be a unique attr, reference or marked as `:db/index true`
       (datoms db :avet :likes \"pizza\")
       ; => (#datahike/Datom [1 :likes \"pizza\"]
       ;     #datahike/Datom [2 :likes \"pizza\"])

       ; find all datoms sorted by entity id, then attribute, then value
       (datoms db :eavt) ; => (...)

   Useful patterns:

       ; get all values of :db.cardinality/many attribute
       (->> (datoms db :eavt eid attr) (map :v))

       ; lookup entity ids by attribute value
       (->> (datoms db :avet attr value) (map :e))

       ; find all entities with a specific attribute
       (->> (datoms db :aevt attr) (map :e))

       ; find “singleton” entity by its attr
       (->> (datoms db :aevt attr) first :e)

       ; find N entities with lowest attr value (e.g. 10 earliest posts)
       (->> (datoms db :avet attr) (take N))

       ; find N entities with highest attr value (e.g. 10 latest posts)
       (->> (datoms db :avet attr) (reverse) (take N))

   Gotchas:

   - Index lookup is usually more efficient than doing a query with a single clause.
   - Resulting iterator is calculated in constant time and small constant memory overhead.
   - Iterator supports efficient `first`, `next`, `reverse`, `seq` and is itself a sequence.
   - Will not return datoms that are not part of the index (e.g. attributes with no `:db/index` in schema when querying `:avet` index).
     - `:eavt` and `:aevt` contain all datoms.
     - `:avet` only contains datoms for references, `:db/unique` and `:db/index` attributes."
  ([db index]             {:pre [(db/db? db)]} (db/-datoms db index []))
  ([db index c1]          {:pre [(db/db? db)]} (db/-datoms db index [c1]))
  ([db index c1 c2]       {:pre [(db/db? db)]} (db/-datoms db index [c1 c2]))
  ([db index c1 c2 c3]    {:pre [(db/db? db)]} (db/-datoms db index [c1 c2 c3]))
  ([db index c1 c2 c3 c4] {:pre [(db/db? db)]} (db/-datoms db index [c1 c2 c3 c4])))

(defn seek-datoms
  "Similar to [[datoms]], but will return datoms starting from specified components and including rest of the database until the end of the index.

   If no datom matches passed arguments exactly, iterator will start from first datom that could be considered “greater” in index order.

   Usage:

       (seek-datoms db :eavt 1)
       ; => (#datahike/Datom [1 :friends 2]
       ;     #datahike/Datom [1 :likes \"fries\"]
       ;     #datahike/Datom [1 :likes \"pizza\"]
       ;     #datahike/Datom [1 :name \"Ivan\"]
       ;     #datahike/Datom [2 :likes \"candy\"]
       ;     #datahike/Datom [2 :likes \"pie\"]
       ;     #datahike/Datom [2 :likes \"pizza\"])

       (seek-datoms db :eavt 1 :name)
       ; => (#datahike/Datom [1 :name \"Ivan\"]
       ;     #datahike/Datom [2 :likes \"candy\"]
       ;     #datahike/Datom [2 :likes \"pie\"]
       ;     #datahike/Datom [2 :likes \"pizza\"])

       (seek-datoms db :eavt 2)
       ; => (#datahike/Datom [2 :likes \"candy\"]
       ;     #datahike/Datom [2 :likes \"pie\"]
       ;     #datahike/Datom [2 :likes \"pizza\"])

       ; no datom [2 :likes \"fish\"], so starts with one immediately following such in index
       (seek-datoms db :eavt 2 :likes \"fish\")
       ; => (#datahike/Datom [2 :likes \"pie\"]
       ;     #datahike/Datom [2 :likes \"pizza\"])"
  ([db index] {:pre [(db/db? db)]} (db/-seek-datoms db index []))
  ([db index c1] {:pre [(db/db? db)]} (db/-seek-datoms db index [c1]))
  ([db index c1 c2] {:pre [(db/db? db)]} (db/-seek-datoms db index [c1 c2]))
  ([db index c1 c2 c3] {:pre [(db/db? db)]} (db/-seek-datoms db index [c1 c2 c3]))
  ([db index c1 c2 c3 c4] {:pre [(db/db? db)]} (db/-seek-datoms db index [c1 c2 c3 c4])))

(def ^:private last-tempid (atom -1000000))

(defn tempid
  "Allocates and returns an unique temporary id (a negative integer). Ignores `part`. Returns `x` if it is specified.

   Exists for Datomic API compatibility. Prefer using negative integers directly if possible."
  ([part]
   (if (= part :db.part/tx)
     :db/current-tx
     (swap! last-tempid dec)))
  ([part x]
   (if (= part :db.part/tx)
     :db/current-tx
     x)))

(def ^{:arglists '([db eid])
       :doc      "Retrieves an entity by its id from database. Entities are lazy map-like structures to navigate DataScript database content.

             For `eid` pass entity id or lookup attr:

                 (entity db 1)
                 (entity db [:unique-attr :value])

             If entity does not exist, `nil` is returned:

                 (entity db -1) ; => nil

             Creating an entity by id is very cheap, almost no-op, as attr access is on-demand:

                 (entity db 1) ; => {:db/id 1}

             Entity attributes can be lazily accessed through key lookups:

                 (:attr (entity db 1)) ; => :value
                 (get (entity db 1) :attr) ; => :value

             Cardinality many attributes are returned sequences:

                 (:attrs (entity db 1)) ; => [:v1 :v2 :v3]

             Reference attributes are returned as another entities:

                 (:ref (entity db 1)) ; => {:db/id 2}
                 (:ns/ref (entity db 1)) ; => {:db/id 2}

             References can be walked backwards by prepending `_` to name part of an attribute:

                 (:_ref (entity db 2)) ; => [{:db/id 1}]
                 (:ns/_ref (entity db 2)) ; => [{:db/id 1}]

             Reverse reference lookup returns sequence of entities unless attribute is marked as `:db/component`:

                 (:_component-ref (entity db 2)) ; => {:db/id 1}

             Entity gotchas:

             - Entities print as map, but are not exactly maps (they have compatible get interface though).
             - Entities are effectively immutable “views” into a particular version of a database.
             - Entities retain reference to the whole database.
             - You can’t change database through entities, only read.
             - Creating an entity by id is very cheap, almost no-op (attributes are looked up on demand).
             - Comparing entities just compares their ids. Be careful when comparing entities taken from differenct dbs or from different versions of the same db.
             - Accessed entity attributes are cached on entity itself (except backward references).
             - When printing, only cached attributes (the ones you have accessed before) are printed. See [[touch]]."}
  entity de/entity)

(defn entity-db
  "Returns a db that entity was created from."
  [^Entity entity]
  {:pre [(de/entity? entity)]}
  (.-db entity))

(defn is-filtered
  "Returns `true` if this database was filtered using [[filter]], `false` otherwise."
  [x]
  (instance? FilteredDB x))

(defn filter
  "Returns a view over database that has same interface but only includes datoms for which the `(pred db datom)` is true. Can be applied multiple times.

   Filtered DB gotchas:

   - All operations on filtered database are proxied to original DB, then filter pred is applied.
   - Not cached. You pay filter penalty every time.
   - Supports entities, pull, queries, index access.
   - Does not support hashing of DB.
   - Does not support [[with]] and [[db-with]]."
  [db pred]
  {:pre [(db/db? db)]}
  (if (is-filtered db)
    (let [^FilteredDB fdb db
          orig-pred (.-pred fdb)
          orig-db (.-unfiltered-db fdb)]
      (FilteredDB. orig-db #(and (orig-pred %) (pred orig-db %))))
    (FilteredDB. db #(pred db %))))

(defn- is-temporal? [x]
  (or (instance? HistoricalDB x)
      (instance? AsOfDB x)
      (instance? SinceDB x)))

(defn with
  "Same as [[transact]], but applies to an immutable database value. Returns transaction report (see [[transact]])."
  ([db tx-data] (with db tx-data nil))
  ([db tx-data tx-meta]
   {:pre [(db/db? db)]}
   (if (or (is-filtered db) (is-temporal? db))
     (throw (ex-info "Filtered DB cannot be modified" {:error :transaction/filtered}))
     (db/transact-tx-data (db/map->TxReport
                            {:db-before db
                             :db-after  db
                             :tx-data   []
                             :tempids   {}
                             :tx-meta   tx-meta}) tx-data))))

(defn db-with
  "Applies transaction to an immutable db value, returning new immutable db value. Same as `(:db-after (with db tx-data))`."
  [db tx-data]
  {:pre [(db/db? db)]}
  (:db-after (with db tx-data)))

(defn db
  "Returns the current state of the database you may interact with."
  [conn]
  @conn)

(defn history
  "Returns the full historical state of the database you may interact with."
  [db]
  (if (db/-temporal-index? db)
    (HistoricalDB. db)
    (throw (ex-info "as-of is only allowed on temporal indexed databases." {:config (db/-config db)}))))

(defn- date? [d]
  #?(:cljs (instance? js/Date d)
     :clj  (instance? Date d)))

(defn as-of
  "Returns the database state at given Date (you may use either java.util.Date or Epoch Time as long)."
  [db date]
  {:pre [(or (int? date) (date? date))]}
  (if (db/-temporal-index? db)
    (AsOfDB. db (if (date? date) date (java.util.Date. ^long date)))
    (throw (ex-info "as-of is only allowed on temporal indexed databases." {:config (db/-config db)}))))

(defn since
  "Returns the database state since a given Date (you may use either java.util.Date or Epoch Time as long).
  Be aware: the database contains only the datoms that were added since the date."
  [db date]
  {:pre [(or (int? date) (date? date))]}
  (if (db/-temporal-index? db)
    (SinceDB. db (if (date? date) date (java.util.Date. ^long date)))
    (throw (ex-info "since is only allowed on temporal indexed databases." {:config (db/-config db)}))))
