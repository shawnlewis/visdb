(ns visdb.client.main
    (:use [jayq.core :only [$ append delegate data]])
    (:require [crate.core :as crate])
    )

(defn log [obj]
    (.log js/console (pr-str obj)))

;(js/alert "hey!")

(def $elements ($ :#elements))

(def textbox-element [:input {:type "text"}])

(def radio-element [:input {:type "radio"}])

(def elements [textbox-element radio-element])

(doseq [e elements]
    (append $elements (crate/html e)))

; Elements pane:
;    - text box, check box, that can be dragged to page
;
