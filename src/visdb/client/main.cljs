(ns visdb.client.main
    (:use [jayq.util :only [clj->js]])
    (:require [jayq.core :as jayq]
              [crate.core :as crate]
              [clojure.browser.repl :as repl]
              [goog.fx.DragDrop :as DragDrop]
              [goog.events :as events]
              [goog.style :as style]
              )
    )

(repl/connect "http://localhost:9000/repl")

;;; utilities

(defn log [obj]
    (.log js/console (pr-str obj)))

(defn jslog [obj]
    (.log js/console (clj->js obj)))

(defn indexed [seq]
    (map-indexed vector seq)) 

;;; elements panel

(def $control-templates (jayq/$ :#control-templates))
(def $record (jayq/$ :#record))

(def textbox-control
    {:template-html [:input.draggable {:type "text"}]})

(def radio-control
    {:template-html [:input.draggable {:type "radio"}]})

(def image-control
    {:template-html [:img.draggable {:src "/img/goog-icon.png"}]})

(def controls
    (let [controls [textbox-control radio-control image-control]]
        (for [c controls]
            (assoc c
                   :template-el
                   (crate/html (:template-html c))))))

(doseq [c controls]
    (jayq/append $control-templates (:template-el c)))

;;; enable drag and drop


(def records (atom []))
(def cur-record-index (atom nil))
(defn cur-record [] (nth @records @cur-record-index))
(defn update-cur-record! [record]
    (set-atom! records (assoc @records @cur-record-index record)))

(defn set-atom! [atom val]
    (swap! atom (fn [] val)))

(defn add-record []
    (let [record []]
        (set-atom! records (conj @records record))
        (set-atom! cur-record-index (dec (count @records)))))

(defn add-control [record control coord]
    (conj record [control coord]))

(defn render []
    (let [record-list (jayq/$ "#record-list")]
        (jayq/empty record-list)
        (doseq [[i _] (indexed @records)]
            (jayq/append record-list
                (-> (jayq/$ "<li>")
                    (jayq/text (str "record" i))
                    (jayq/data "record" i))))
        (jayq/empty $record)
        (doseq [[control coord] (cur-record)]
            (let [new-el (crate/html (:template-html control))]
                (set-css (jayq/$ new-el)
                    {"position" "absolute"
                     "left" (str coord.x "px")
                     "top" (str coord.y "px")})
                (jayq/append $record new-el)))))

(add-record)
(add-record)

(render)

(jayq/bind (jayq/$ "#add-record") :click
    #(do (add-record) (render)))

(jayq/on
    (jayq/$ "#record-list-pane")
    :click
    "li"
    #(do
        (set-atom! cur-record-index
            (-> %
                .-target
                jayq/$
                (jayq/data "record")))
        (render)))

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

(def drags (map #(make-drag-drop (:template-el %) %) controls))
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
    (let [control event.dragSourceItem.data
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
        (do
            (update-cur-record! (add-control (cur-record) control rel-coord))
            (render))))

(events/listen drop "drop" on-drop)

; Elements pane:
;    - text box, check box, that can be dragged to page
;
