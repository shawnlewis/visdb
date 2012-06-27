(ns visdb.client.util
    (:use [jayq.util :only [clj->js]]))

(defn log [obj]
    (.log js/console (pr-str obj)))

(defn jslog [obj]
    (.log js/console (clj->js obj)))
