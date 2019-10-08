(ns datahike.constants)

(def ^:const txmax 0xFFFFFFFFFFF)
(def ^:const tx0 0x80000000000)
(def ^:const e0  0x100000000000)
(def ^:const emax 0xFFFFFFFFFFFF)

(def ^:const implicit-schema {:db/ident {:db/unique :db.unique/identity}})
