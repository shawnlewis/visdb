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

(def controls [textbox-control radio-control])

(doseq [c controls]
    (append $control-templates (crate/html (:template-html c))))


;;; enable drag and drop

(defn drag-drop [drag-els drop-el]
    (let [drop (goog.fx.DragDrop. drop-el)
          drags (map #(goog.fx.DragDrop. % "hello") drag-els)]
        (do 
            (doseq [d drags] (.addTarget d drop))
            (doseq [d drags] (.init d))
            drags
            )))

(def drags (drag-drop ($ :.draggable) "drop-area"))

(defn on-drop [event]
    (let [drag-el event.dragSourceItem.data]
        (jslog drag-el)))

(events/listen (first drags) "dragstart" on-drop)
(events/listen (second drags) "dragstart" on-drop)

; Elements pane:
;    - text box, check box, that can be dragged to page
;
