(ns visdb.client.main
    (:use [jayq.core :only [$ append delegate data]]
          [jayq.util :only [clj->js]])
    (:require [crate.core :as crate]
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


;;; elements panel

(def $control-templates ($ :#control-templates))
(def $drop-area ($ :#drop-area))

(def textbox-control
    {:template-html [:input.draggable {:type "text"}]})

(def radio-control
    {:template-html [:input.draggable {:type "radio"}]})

(def controls
    (let [controls [textbox-control radio-control]]
        (for [c controls]
            (assoc c
                   :template-el
                   (crate/html (:template-html c))))))

(doseq [c controls]
    (append $control-templates (:template-el c)))

;;; enable drag and drop

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
(def drop (make-drag-drop "drop-area"))
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
          rel-coord (coord-difference
                     drop-client-coord
                     (style/getClientPosition drop-el))
          new-el (crate/html (:template-html control))
          ]
        (do
            (jslog rel-coord)
            (set-css ($ new-el)
                     {"position" "absolute"
                      "left" (str rel-coord.x "px")
                      "top" (str rel-coord.y "px")})
            (append $drop-area new-el))))

(events/listen drop "drop" on-drop)
(events/listen drop "drop" on-drop)

; Elements pane:
;    - text box, check box, that can be dragged to page
;
