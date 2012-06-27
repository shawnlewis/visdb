;;; main.cljs
;;; =========

;(def cur-record-index (atom nil))
;(defn cur-record [] (nth @records @cur-record-index))
;(defn update-cur-record! [record]
;    (set-atom! records (assoc @records @cur-record-index record)))
;
;(defn set-atom! [atom val]
;    (swap! atom (fn [] val)))
;
;(defn add-record []
;    (let [record []]
;        (set-atom! records (conj @records record))
;        (set-atom! cur-record-index (dec (count @records)))))
;
;(defn add-control [record control coord]
;    (conj record [control coord]))



;;; model.cljs
;;; ===========

;;;; table: type | group | class | stack | book | collection | set | category
;;           kind
;
;;;; table attributes (and meta-info): spec | schema | template
;;;; single attribute: field
;
;;;; row in a table: record | page | item | card
;;;; cell in a row: value
;
;;;;
;;;; "type-spec"
;
;
;;; (make-record)
;;; (add-control)
;
;
;;;; data model
;;; {:kinds ["Company", "Idea", "Tag"],
;;    :Company {
;;    }
;
;;; data: {} of (kind, kind-spec) pairs
;;; kind-spec: {:template [field-template, ...]
;               :cards [card, ...]}
;;; field-template: {:type (img, date, text, choice, link),
;;;                  :position (x, y)
;;;                  :properties {}}
;;; card: [field-val-tuple, ...]
;;; field-val-tuple: a list of whatever the field needs to render
;
;
;;; kind
;;; card
;;; field
;
;
;;; could keep one flat list of cards if each card has a type associated with
;;; it
;
;
;; relational design
;; kind: id, name
;; field-template: id, kind-id, type, position, (property keys...)
;; card: id, kind-id
;; field: id, card-id, field-template-id, value
;
;
;
;
;;(defn create-deck [] {})
;;(defn create-kind-spec []
;;    {:template []
;;     :cards []})
;;(defn create-field [type position]
;;    {:type type
;;     :position position
;;     :properties {}})
;;
;;(defn create-card [] {})
;;
;;(defn add-kind [deck kind-name] (assoc deck kind-name (create-kind-spec)))
;;
;;(defn add-field [deck kind-name field]
;;    (let [kind-spec (get deck kind-name)]]
;;    (assoc deck kind-name
;;        (assoc kind-spec :template
;;            (conj (:template kind-spec) field)))))
;;
;;(defn add-card [deck kind-name card]
;;    (assoc deck kind-name
;;        (assoc (deck kind-name) :cards
;;            (conj ((deck kind-name) :cards) card))))
;;
;;(defn add-field-val [deck kind-name card-index field-val]
;;    (assoc deck kind-name
;;        (assoc (deck kind-name) :cards
;;            (assoc ((deck kind-name) :cards) card-index field-val))))
;;
;
;;;; question:
;;;; say we have a list of people (which are maps)
;;;;     and a list of movies (also maps)
;;;; each person map has a key :movies which is a vector of movie maps the
;;;;     person likes
;;;; now if we update 
;
;
;;;; user code:
;;
;; (on-drop
;;   (fn [e] add-field (data cur-deck-name kind-name card-num (field-template e))))



;;; Playing with core.logic
;;; =======================

;(def policies (ref #{{:id 3 :name "x" :holder 7 :vehicle 11} 
;                     {:id 4 :name "y" :holder 2 :vehicle 12}})) 
;
;(def vehicles (ref #{{:id 11 :make "Audi" :driver 7} 
;                     {:id 12 :make "Toyota" :driver 2}})) 
;
;(def people (ref #{{:id 7 :name "Brenton"} 
;                   {:id 2 :name "John"}})) 
;
;(defrel person-name id name)
;(facts person-name [[7 "Brenton"]
;                    [2 "John"]])
;
;(defrel vehicle-make id make)
;(facts vehicle-make [[11 "Audi"]
;                     [12 "Toyota"]])
;
;(defrel vehicle-driver id driver-id)
;(facts vehicle-driver [[11 7]
;                       [12 2]])
;
;(defrel policy-name id name)
;(facts policy-name [[3 "x"]
;                    [4 "y"]])
;
;(defrel policy-holder id holder-id)
;(facts policy-holder [[3 7]
;                      [4 2]])
;
;(defrel policy-vehicle id vehicle-id)
;(facts policy-vehicle [[3 11]
;                       [4 12]])
;
;                     
;
;;; return name of person who drives an Audi
;
;(run* [q]
;   (fresh [p v]
;       (vehicle-make v "Audi")
;       (vehicle-driver v p)
;       (person-name p q)))
;
;
;;; Two ways to define this:
;;;    1) each kind is actually a table, in this sense the user would be
;;;       defining the actual schema of a db.
;;;    2) represent the schema within the db, rather than as the db's schema
;
;
;;; Way #2
;;;
;;;
;(use 'clojure.core.logic)
;(defrel kind id name)
;(defrel field-template id kind-id name type position)
;(defrel card id kind-id)
;(defrel field id card-id field-template-id value)
;
;(facts kind
;       [[1 "Company"]
;        [2 "Data Source"]
;        [3 "Tag"]])
;
;(facts field-template
;       [[1 1 "Name" "string" [0 0]]
;        [2 1 "Funding" "number" [0 30]]
;        [3 1 "Logo" "image" [200 0]]
;
;        [4 2 "Name" "string" [0 0]]
;
;        [5 3 "Name" "string" [0 0]]])
;
;(facts card
;       [[1 1]
;        [2 1]
;        [3 2]
;        [4 2]
;        [5 3]
;        [6 3]])
;
;(facts field
;       [[1 1 1 "Singly"]
;        [2 1 2 2100000]
;        [3 1 3 "singly.png"]
;
;        [4 2 1 "Ark"]
;        [5 2 2 4200000]
;        [6 2 3 "ark.png"]
;
;        [7 3 4 "Facebook"]
;        [8 4 4 "LinkedIn"]
;
;        [9 5 5 "source-crawled"]
;        [10 6 5 "source-queried"]])
;
;
;; get field-templates and field-values for a given card
;(run* [q]
;   (field {:id 5 :card-id cid :value fv})
;   (field-template {:id cid :type ftt :pos ftp}))
;
;; get field-id and field-template-id for a given card
;(run* [q]
;      (fresh [field-id field-template-id b]
;          (field field-id 2 field-template-id b)
;          (conso field-id field-template-id q)))
;
;; get field-name field-type field-value for a given card
;(run* [q]
;      (fresh [field-template-id name type value a b c d]
;          (field a 2 field-template-id value)
;          (field-template field-template-id b name type d)
;          (== [type name value] q)))
;
;; prettier version without free vars
;(run* [q]
;      (fresh field-template-id name type value]
;          (field {:card-id 2
;                  :field-template-id field-template-id
;                  :value value})
;          (field-template {:id field-template-id
;                           :name name
;                           :type type}))
;          (== [type name value] q))
;
;; version that:
;;   - replaces rel calls with calls that utilize free variables
;;   - wraps with fresh to produce all neeeded free vars
;;
;;
;;
;
;
;
;; How will client code call this?
;;
;; on value change:
;;   (update-fact field (get-field-id e) {
;
;
;; database
;
;(defn create-database [] {:id 0 :relations {}})
;
;(defn add-relation [database relname & attrs]
;    (let [schema (conj (set (map keyword attrs)) :id)]
;        (assoc-in database [:relations relname] 
;               {:schema schema 
;                :records []})))
;
;(defn insert [database relname record]
;    (let [new-id (inc (database :id))
;          with-id (assoc record :id new-id)
;          records (get-in database [:relations relname :record])
;          new-records (conj records with-id)]
;    (assoc (assoc-in database [:relations relname :record] new-records)
;           :id new-id)))
;
;(comment 
;  (def db (create-database))
;  (println db)
;
;  (def db-company (add-relation (create-database) "Company" "name")) 
;  (println db-company)
;
;  (def db-singly 
;      (insert db-company "Company" {:name "Singly"})) 
;  (println db-singly)
;
;  (println (insert db-singly "Company" {:name "Youtube"}))
;)
