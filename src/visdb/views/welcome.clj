(ns visdb.views.welcome
  (:require [visdb.views.common :as common]
            [noir.content.getting-started])
  (:use [noir.core :only [defpage]]
        [hiccup.core :only [html]]))

(defpage "/welcome" []
         (common/layout
           [:p "Welcome to visdb"]
           [:div.half
             [:div#control-templates]]
           [:div.half
             [:div#record-list-pane
              [:button#add-record "Add Record"]
              [:ul#record-list]]]
           [:div.whole
             [:div#record]]
           ))
