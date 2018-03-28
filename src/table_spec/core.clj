(ns table-spec.core
  (:require [clojure.java.jdbc :as jdbc]
            [clojure.spec.alpha :as s]
            [clojure.set :as set]
            [clojure.string :as str]
            [clojure.edn :as edn]
            [clojure.spec.gen.alpha :as gen])
  (:import [java.sql Types]))


;(extend-protocol clojure.java.jdbc/ISQLParameter
;  clojure.lang.IPersistentVector
;  (set-parameter [v ^java.sql.PreparedStatement stmt ^long i]
;    (let [conn (.getConnection stmt)
;          meta (.getParameterMetaData stmt)
;          type-name (.getParameterTypeName meta i)]
;      (if-let [elem-type (when (= (first type-name) \_) (apply str (rest type-name)))]
;        (.setObject stmt i (.createArrayOf conn elem-type (to-array v)))
;        (.setObject stmt i v)))))
;
;(extend-protocol clojure.java.jdbc/IResultSetReadColumn
;  java.sql.Array
;  (result-set-read-column [val _ _]
;    (into [] (.getArray val))))



(defmulti data-type :data_type)

(defn unknown-data-type-ex [{:keys [data_type] :as m}]
  (println (str "Undefined data type: " data_type) m))
  ;;(ex-info (str "Undefined data type: " data_type) m))

;; Numbers

(defmethod data-type Types/INTEGER [_]
  (s/spec int?))

(defmethod data-type Types/SMALLINT [_]
  (s/spec (s/int-in -32768 32767)))

(defmethod data-type Types/BIGINT [_]
  (s/spec (s/int-in -9223372036854775808 9223372036854775807)))

(defmethod data-type Types/DOUBLE [_]
  (s/spec double?))

(defmethod data-type Types/FLOAT [_]
  (s/spec double?))

(defmethod data-type Types/REAL [_]
  (s/spec double?))

(defmethod data-type Types/NUMERIC [{:keys [decimal_digits]}]
  (s/spec decimal?
          :gen (fn []
                 (gen/fmap #(.setScale (BigDecimal/valueOf ^Double %)
                                       decimal_digits java.math.RoundingMode/UP)
                           (gen/double* {:infinite? false :NaN? false})))))

;; Data and time

(s/def ::timestamp
  (s/spec #(instance? java.sql.Timestamp %)
          :gen (fn []
                 (gen/fmap #(java.sql.Timestamp. ^Long %)
                           (gen/large-integer)))))

(defmethod data-type Types/TIMESTAMP [_]
  (s/get-spec ::timestamp))

(defmethod data-type Types/TIMESTAMP_WITH_TIMEZONE [_]
  (s/get-spec ::timestamp))

(defmethod data-type Types/DATE [_]
  (s/spec #(instance? java.sql.Date %)
          :gen (fn []
                 (gen/fmap #(java.sql.Date. ^Long %)
                           (gen/large-integer)))))

;; Strings

(defmethod data-type Types/VARCHAR [{:keys [column_size]}]
  (s/spec (s/and string?
                 #(<= (.length %) column_size))))

(defmethod data-type Types/CHAR [_]
  (s/spec char?))

;; Other

(defmethod data-type Types/BIT [_]
  (s/spec boolean?))

(defmethod data-type Types/BOOLEAN [_]
  (s/spec boolean?))
(s/def ::int4 (s/int-in -2147483648 2147483647))

(s/def ::int4-array (s/coll-of ::int4))

(defmethod data-type 2003 [{:keys [type_name] :as m}]
  (do
    (println "------------------------got there")
    (println "error: " m)))
    ;(case type_name
    ;  "_int4"
    ;  (s/spec ::int4-array)
    ;  (println "error: " e))))


      ;(throw (unknown-data-type-ex m)))))




(s/def ::ip-address
  (letfn [(pred [s]
            (let [parts (str/split s #"\.")]
              (and (= (count parts) 4)
                   (every? (fn [part]
                             (try
                               (let [n (edn/read-string part)]
                                 (and (integer? n)
                                      (>= 256 n 0)))
                               (catch Exception _ false)))
                           parts))))
          (gen []
            (gen/fmap
              (partial str/join ".") (gen/vector (gen/choose 0 255) 4)))]
    (s/spec pred :gen gen)))


(defmethod data-type Types/OTHER [{:keys [type_name] :as m}]
  (case type_name
    "uuid"  (s/spec uuid?)
    "inet"  (s/spec ::ip-address)
    (println "cant match: " m)))

    ;(throw (unknown-data-type-ex m))))






(defmethod data-type :default [m]
  (println "undefined data type: " m))
  ;(throw (unknown-data-type-ex m)))

;; End data type defs

(defn table-meta [md schema]
  (-> md
      (.getColumns nil schema nil nil)
      (jdbc/metadata-result)
      (#(group-by :table_name %))))

(defn tables [{:keys [schema] :as db-spec}]
  (jdbc/with-db-metadata [md db-spec]
    (for [[table columns] (table-meta md schema)]
      (reduce (fn [acc {:keys [column_name] :as column}]
                (let [k (keyword table column_name)]
                  (-> acc
                      (update :specs assoc k (data-type column))
                      (update :opts #(if (= "NO" (:is_nullable column))
                                       (update % :req conj k)
                                       %)))))
              {:table table
               :specs {}
               :opts {:req #{}}}
              columns))))

(defn register [table]
  (doseq [{:keys [table specs opts]} table]
    (doseq [[k s] specs]
      (eval `(s/def ~k ~s)))
    (let [required-keys# (-> opts :req vec)
          optional-keys# (-> specs keys set (set/difference required-keys#) vec)]
      (eval `(s/def ~(keyword "table" table) (s/keys :req ~required-keys#
                                                     :opt ~optional-keys#))))))
