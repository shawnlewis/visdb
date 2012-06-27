(ns visdb.client.model
    (:use [visdb.client.util :only [jslog]]))

(defn create-database [] {:id 0 :relations {}})

(defn add-relation [database relname & attrs]
    (let [schema (conj (set (map keyword attrs)) :id)]
        (assoc-in database [:relations relname] 
               {:schema schema 
                :records []})))

(defn insert [database relname record]
    (let [new-id (inc (database :id))
          with-id (assoc record :id new-id)
          records (get-in database [:relations relname :records])
          new-records (conj records with-id)
          new-database (assoc-in database [:relations relname :records]
                                 new-records)]
        [(assoc new-database :id new-id) new-id]))

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

(! db add-relation "field-template" :control-type :position)
(! db add-relation "kind" :name)
(! db add-relation "card" :kind-id)
