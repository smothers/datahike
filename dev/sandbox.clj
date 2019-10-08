(ns sandbox
  (:require [datahike.api :as d]
            [datahike.migration :as m]))

(comment

  (def uri "datahike:mem://sandbox")

  (d/delete-database uri)

  (def schema [{:db/ident :name
                :db/cardinality :db.cardinality/one
                :db/index true
                :db/unique :db.unique/identity
                :db/valueType :db.type/string}
               {:db/ident :sibling
                :db/cardinality :db.cardinality/many
                :db/valueType :db.type/ref}
               {:db/ident :age
                :db/cardinality :db.cardinality/one
                :db/valueType :db.type/long}])

  (d/create-database uri :initial-tx schema)

  (def conn (d/connect uri))

  (def result (d/transact conn [{:name  "Alice", :age   25}
                                {:name  "Bob", :age   35}
                                {:name "Charlie", :age 45 :sibling [[:name "Alice"] [:name "Bob"]]}]))

  (d/q '[:find ?e ?a ?v ?t :where [?e ?a ?v ?t ?tx]] @conn)

  (m/export-db @conn "/tmp/dh_export")

  (def uri-2 "datahike:mem://sandbox-2")

  (d/delete-database uri-2)

  (d/create-database uri-2)


  (def conn-2 (d/connect uri-2))

  (d/transact conn-2 [{:name "foo"}])

  (m/import-db conn-2 "/tmp/dh_export")

  (d/q '[:find ?e ?v ?t :where [?e :name ?v ?t]] @conn-2)

  )
