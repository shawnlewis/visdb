(ns visdb.client.main
    (:use [jayq.core :only [$ append delegate data]]
          [jayq.util :only [clj->js]])
    (:require [crate.core :as crate]
              [clojure.browser.repl :as repl]
              [goog.fx.DragDrop :as DragDrop]
              [goog.events :as events]
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

; goal
;   each control-template has a draggable control created
;   on drop a new object is created from the template

(defn make-drag-drop
    ([el]
      (goog.fx.DragDrop. el))
    ([el data]
      (goog.fx.DragDrop. el data)))

(defn drag-drop [drags drop]
    (do
        (doseq [d drags] (.addTarget d drop))
        (doseq [d drags] (.init d))
        drags
        ))

(def drags
    (drag-drop
        (map #(make-drag-drop (:template-el %) %) controls)
        (make-drag-drop "drop-area")))

(defn on-drop [event]
    (let [drag-el event.dragSourceItem.data]
        (jslog drag-el)))

(events/listen (first drags) "dragstart" on-drop)
(events/listen (second drags) "dragstart" on-drop)

; Elements pane:
;    - text box, check box, that can be dragged to page
;
