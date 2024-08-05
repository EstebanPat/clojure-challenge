(ns problem1)

(require '[clojure.edn :as edn])

(def invoice (edn/read-string (slurp "../invoice.edn")))

(defn valid-item?
  [item]
  (let [has-iva-19%  (->> (:taxable/taxes item)
                          (some #(and (= (:tax/category %) :iva)
                                      (= (:tax/rate %) 19))))
        has-ret-1%   (->> (:retentionable/retentions item)
                          (some #(and (= (:retention/category %) :ret_fuente)
                                      (= (:retention/rate %) 1))))]
    (and (or has-iva-19% has-ret-1%)
         (not (and has-iva-19% has-ret-1%)))))

(defn valid-items [invoice]
  (->> (:invoice/items invoice)
       (filter valid-item?)))

(def valid-items-list (valid-items invoice))

(prn valid-items-list)