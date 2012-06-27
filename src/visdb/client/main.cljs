(ns visdb.client.main
    (:use [visdb.client.util :only [jslog]])
    (:require [visdb.client.model :as model]
              [jayq.core :as jayq]
              [crate.core :as crate]
              [clojure.browser.repl :as repl]
              [goog.fx.DragDrop :as DragDrop]
              [goog.events :as events]
              [goog.style :as style]))

(repl/connect "http://localhost:9000/repl")

;;; utilities

(defn indexed [seq]
    (map-indexed vector seq)) 

;;; elements panel

(def $control-templates (jayq/$ :#control-templates))
(def $record (jayq/$ :#record))

(def controls
    (let [controls
          {:text-box {:template-html [:input.draggable {:type "text"}]}
           :radio {:template-html [:input.draggable {:type "radio"}]}
           :image {:template-html
                   [:img.draggable {:src "/img/goog-icon.png"}]}}
          add-el (fn [controls key]
                    (assoc-in controls [key :template-el]
                        (crate/html (get-in controls [key :template-html]))))]

        (reduce add-el controls (keys controls))))

(doseq [c (vals controls)]
    (jayq/append $control-templates (:template-el c)))

;;; enable drag and drop


(def records (atom []))

(defn render [db]
    (let [record-list (jayq/$ "#record-list")
          kind-list (jayq/$ "#kind-list")]
        (jayq/empty record-list)
        (doseq [card (filter #(== (:kind-id %) (:current-kind @*user-state*))
                             (model/get-records db "card"))]
            (jayq/append record-list
                (-> (jayq/$ "<li>")
                    (jayq/text (str "record " (:id card)))
                    (jayq/data "record" (:id card)))))

        (jayq/empty kind-list)
        (doseq [kind (model/get-records db "kind")]
            (jayq/append kind-list
                (-> (jayq/$ "<li>")
                    (jayq/text (str (:name kind)))
                    (jayq/data "id" (:id kind)))))

        (jayq/empty $record)
        (doseq [{control-type :control-type position :position}
                (model/get-records db "field-template")]
            (let [control (controls control-type)
                  new-el (crate/html (:template-html control))]
                (set-css (jayq/$ new-el)
                    {"position" "absolute"
                     "left" (str position.x "px")
                     "top" (str position.y "px")})
                (jayq/append $record new-el)))))

(def *user-state* (atom {}))
(defn set-user-state! [k v]
    (reset! *user-state* (assoc @*user-state* k v)))

(defn add-kind [name]
    (let [kind-id (model/! model/db model/insert "kind" {:name name})]
        (set-user-state! :current-kind kind-id)
        (render @model/db)))

(defn add-record []
    (model/! model/db model/insert "card"
     {:kind-id (:current-kind @*user-state*)}))

(model/on-change model/db render)

(add-kind "Blank")

(add-record)
(add-record)

(jayq/bind (jayq/$ "#add-record") :click add-record)
(jayq/bind (jayq/$ "#add-kind") :click
    #(add-kind (jayq/val (jayq/$ "#kind-name"))))

(jayq/on
    (jayq/$ "#kind-list-pane")
    :click
    "li"
    (fn [event]
        (do
            (set-user-state! :current-kind
                (-> event.target
                    jayq/$
                    (jayq/data "id")))
            (render @model/db))))

;(jayq/on
;    (jayq/$ "#record-list-pane")
;    :click
;    "li"
;    #(do
;        (set-atom! cur-record-index
;            (-> %
;                .-target
;                jayq/$
;                (jayq/data "record")))
;        (render)))
;
(defn make-drag-drop
    ([el]
      (goog.fx.DragDrop. el))
    ([el data]
      (goog.fx.DragDrop. el data)))

(defn drag-drop [drags drop]
    (do
        (doseq [d drags] (.addTarget d drop))
        (doseq [d drags] (.init d))
        ))

(def drags
    (map #(make-drag-drop (get-in controls [% :template-el]) %)
         (keys controls)))

(def drop (make-drag-drop "record"))
(drag-drop drags drop)

(defn make-coord [x y]
    (goog.math.Coordinate. x y))

(defn coord-difference [a b]
    (goog.math.Coordinate/difference a b))

(defn set-css [$elem css-map]
  (doseq [[k v] css-map]
    (.css $elem k v)))

(defn on-drop [event]
    (let [control-type event.dragSourceItem.data
          drop-el event.dropTargetElement
          drop-client-coord (make-coord event.clientX event.clientY)
          mouse-offset (coord-difference
                        event.dragSourceItem.startPosition_
                        (style/getClientPosition event.dragSourceItem.element))
          rel-coord (coord-difference
                        (coord-difference
                             drop-client-coord
                             (style/getClientPosition drop-el))
                        mouse-offset)
          ]
        (model/! model/db model/insert "field-template"
             {:control-type control-type :position rel-coord})))

(events/listen drop "drop" on-drop)
