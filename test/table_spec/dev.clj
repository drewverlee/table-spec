(ns table-spec.dev
  (:require [table-spec.core :as t]
            [clojure.spec.alpha :as s]))




;(s/exercise :table/customers)

;([#:customers{:mfa false,
;              :key #uuid"22c07e92-93a1-4367-afbf-9ce9dadb30fe",
;              :root_feature_id 0,
;              :name "",
;              :distributor_id -1,
;              :fiscal_year_start 0,
;              :updated_at #inst"1969-12-31T23:59:59.999000000-00:00",
;              :active true,
;              :id 0,
;              :resolute_start_date #inst"1970-01-01T00:00:00.000000000-00:00",
;              :uuid #uuid"bf7c96d7-a6af-4016-8901-3b7c2899ae91",
;              :created_at #inst"1969-12-31T23:59:59.999000000-00:00"}
;  #:customers{:mfa false,
;              :key #uuid"22c07e92-93a1-4367-afbf-9ce9dadb30fe",
;              :root_feature_id 0,
;              :name "",
;              :distributor_id -1,
;              :fiscal_year_start 0,
;              :updated_at #inst"1969-12-31T23:59:59.999000000-00:00",
;              :active true,
;              :id 0,
;              :resolute_start_date #inst"1970-01-01T00:00:00.000000000-00:00",
;              :uuid #uuid"bf7c96d7-a6af-4016-8901-3b7c2899ae91",
;              :created_at #inst"1969-12-31T23:59:59.999000000-00:00"}])


;(s/exercise :table/nodes)

;=>
;([#:nodes{:updated_at #inst"1970-01-01T00:00:00.000000000-00:00",
;          :customer_uuid #uuid"0b3efaf2-da7b-4d1a-a7ef-b643bd18c0af",
;          :uuid #uuid"f553a992-f65a-4d2f-a32c-513caee5582a",
;          :created_at #inst"1969-12-31T23:59:59.999000000-00:00",
;          :display_name "",
;          :customer_id -1}
;  #:nodes{:updated_at #inst"1970-01-01T00:00:00.000000000-00:00",
;          :customer_uuid #uuid"0b3efaf2-da7b-4d1a-a7ef-b643bd18c0af",
;          :uuid #uuid"f553a992-f65a-4d2f-a32c-513caee5582a",
;          :created_at #inst"1969-12-31T23:59:59.999000000-00:00",

;          :display_name "",
;          :customer_id -1}])

; (-> {:connection-uri "jdbc:postgresql://localhost:5439/resolute_cloud_dev?user=postgres" :schema "public"}
;     (t/tables)
;     (t/register))

;(s/exercise :table/point_types)

;=>
;([#:point_types{:name ""} #:point_types{:name ""}
;  [{} {}]
;  [#:point_types{:name "", :id 0} #:point_types{:name "", :id 0}]
;  [#:point_types{:id -1, :name "aT"} #:point_types{:id -1, :name "aT"}]
;  [#:point_types{:name ""} #:point_types{:name ""}]
;  [#:point_types{:id 3} #:point_types{:id 3}]
;  [#:point_types{:id -1} #:point_types{:id -1}]
;  [#:point_types{:id 0, :name "wwrvohK"} #:point_types{:id 0, :name "wwrvohK"}]
;  [#:point_types{:id 20, :name "0O"} #:point_types{:id 20, :name "0O"}]
;  [{} {}]])

; point types isn't what we need


; (s/def ::table/point_types (s/keys))

; (s/exercise :table/nodes)

; (s/exercise :table/points)

