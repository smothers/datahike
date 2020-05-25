(ns datahike.datom)

(defmacro combine-cmp [& comps]
  (loop [comps (reverse comps)
        res (num 0)]
    (if (not-empty comps)
      (recur
        (next comps)
        `(let [c# ~(first comps)]
          (if (== 0 c#)
            ~res
            c#)))
      res)))