(ns visdb.client.model
    (:use [visdb.client.util :only [jslog log]]))

(defn create-database [] {:id 0 :relations {}})

(defn add-relation [database relname & attrs]
    (let [schema (conj (set (map keyword attrs)) :id)]
        (assoc-in database [:relations relname] 
               {:schema schema 
                :records []})))

;; really an upsert
(defn insert [database relname record]
    (let [new? (:id record)
          id (if new? (:id record) (inc (:id database)))
          with-id (assoc record :id id)
          records (get-in database [:relations relname :records])
          new-records (if new?
                          (assoc records (index-of records {:id id}) with-id)
                          (conj records with-id))
          new-database (assoc-in database [:relations relname :records]
                                 new-records)]
        [(assoc new-database :id id) id]))

(defn set-atom! [atom val]
    (swap! atom (fn [] val)))

(def db (atom (create-database)))

(defn ! [db-atom fn & args]
    (let [result (apply fn @db-atom args)
          [new-db return] (if (vector? result)
                              [(first result) (second result)]
                              [result nil])]
        (set-atom! db-atom new-db)
        return))

(defn on-change [database callback]
    (let [watch-fn (fn [key ref old new] (callback new))]
        (add-watch database :watch watch-fn)))

(defn get-records [database relname]
    (get-in database [:relations relname :records]))

(defn index-of [s submap]
    (first
        (map first
            (filter #(submap? submap (second %))
                (map-indexed vector s)))))

;; True if a is a submap of b
(defn submap? [a b]
    (= (select-keys b (keys a)) a))

;;; from IRC. apparently this is lazier:
;(every? (partial apply =) (map (juxt m1 m2) (keys m1)))) {:foo 2 :bar 6} {:bar 6 :bam 23 :foo 2})

(defn get-one [database relname sub-match]
    (first (filter (partial submap? sub-match)
                   (get-records database relname))))

(! db add-relation "field-template" :control-type :position :kind-id)
(! db add-relation "kind" :name)
(! db add-relation "card" :kind-id)
(! db add-relation "field-val" :field-id :card-id :value)
