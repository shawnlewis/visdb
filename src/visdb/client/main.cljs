(ns visdb.client.main
    (:use [jayq.core :only [$ append delegate data]])
    (:require [crate.core :as crate])
    )

;(js/alert "hey!")

(def $elements ($ :#elements))

(append $elements (crate/html [:p "Hello"]))

; Elements pane:
;    - text box, check box, that can be dragged to page
;
