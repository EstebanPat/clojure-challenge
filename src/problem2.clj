(ns problem2
  (:require [clojure.data.json :as json]
            [clojure.java.io :as io]
            [clojure.spec.alpha :as s]
            [clojure.instant :as inst]
            [clojure.string :as str]
            [invoice-spec :as spec]))

(defn parse-date [date-str]
  (inst/read-instant-date (str/replace date-str #"(\d{2})/(\d{2})/(\d{4})" "$3-$2-$1T00:00:00.000Z")))

(defn transform-invoice [invoice]

  {:invoice/issue-date (parse-date (:issue_date invoice))
   :invoice/customer   {:customer/name (:company_name (:customer invoice))
                        :customer/email (:email (:customer invoice))}
   :invoice/items      (mapv (fn [item]
                               {:invoice-item/price   (:price item)
                                :invoice-item/quantity (:quantity item)
                                :invoice-item/sku     (:sku item)
                                :invoice-item/taxes   (mapv (fn [tax]
                                                              {:tax/category (keyword (str/lower-case (:tax_category tax)))
                                                               :tax/rate     (double (:tax_rate tax))})
                                                            (:taxes item))})
                             (:items invoice))})

(defn load-invoice [filename]
  (println "Loading file:" filename)
  (let [invoice-data (-> filename
                         io/file
                         slurp
                         (json/read-str :key-fn keyword))]
    (transform-invoice (:invoice invoice-data))))

(def invoice2 (load-invoice "../invoice.json"))

;; Validamos la factura
(println (s/valid? ::spec/invoice invoice2))  ;; Esto debería imprimir `true` si es válida