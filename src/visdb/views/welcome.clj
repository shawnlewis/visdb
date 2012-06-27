(ns visdb.views.welcome
  (:require [visdb.views.common :as common]
            [noir.content.getting-started])
  (:use [noir.core :only [defpage]]
        [hiccup.core :only [html]]))

(defpage "/welcome" []
         (common/layout
           [:p "Welcome to visdb"]
           [:div.third
             [:div#control-templates]]
           [:div.third
             [:div#kind-list-pane
               [:input#kind-name {:type "text"}]
               [:button#add-kind "Add Kind"]
               [:ul#kind-list]]]
           [:div.third
             [:div#record-list-pane
              [:button#add-record "Add Record"]
              [:ul#record-list]]]
           [:div.whole
             [:div#record]]
           ))
