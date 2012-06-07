(ns visdb.views.common
  (:use [noir.core :only [defpartial]]
        [hiccup.page-helpers :only [include-css include-js html5]]))

(defpartial layout [& content]
            (html5
              [:head
               [:title "visdb"]
               (include-css "/css/reset.css")]
              [:body
               [:div#wrapper
                content]
               (include-js "/cljs/all.js")
               ]))
