(ns visdb.client.main
    (:use [jayq.core :only [$ append delegate data]])
    (:require [crate.core :as crate]
              [clojure.browser.repl :as repl]
              [goog.fx.DragDrop :as DragDrop]
              [goog.events :as events]
              )
    )

(repl/connect "http://localhost:9000/repl")

;;; utilities

(defn clj->js
  "Recursively transforms ClojureScript maps into Javascript objects,
   other ClojureScript colls into JavaScript arrays, and ClojureScript
   keywords into JavaScript strings."
  [x]
  (cond
    (string? x) x
    (keyword? x) (name x)
    (map? x) (.-strobj (reduce (fn [m [k v]]
               (assoc m (clj->js k) (clj->js v))) {} x))
    (coll? x) (apply array (map clj->js x))
    :else x))


(defn log [obj]
    (.log js/console (pr-str obj)))

(defn jslog [obj]
    (.log js/console (clj->js obj)))


;;; elements panel

(def $elements ($ :#elements))

(def textbox-element [:input.draggable {:type "text"}])

(def radio-element [:input.draggable {:type "radio"}])

(def elements [textbox-element radio-element])

(doseq [e elements]
    (append $elements (crate/html e)))


;;; enable drag and drop

(defn drag-drop [drag-els drop-el]
    (let [drop (goog.fx.DragDrop. drop-el)
          drags (map #(goog.fx.DragDrop. (aget % 0)) drag-els)]
        (do 
            (doseq [d drags] (.addTarget d drop))
            (doseq [d drags] (.init d))
            drags
            )))

(def drags (drag-drop ($ :.draggable) "drop-area"))

(events/listen (first drags) "dragstart" #(jslog "start1"))
(events/listen (second drags) "dragstart" #(jslog "start2"))

; Elements pane:
;    - text box, check box, that can be dragged to page
;
