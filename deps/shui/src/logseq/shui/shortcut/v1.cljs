(ns logseq.shui.shortcut.v1
  (:require [clojure.string :as string]
            [logseq.shui.ui :as ui]
            [rum.core :as rum]
            [goog.userAgent]))

(def mac? goog.userAgent/MAC)
(defn print-shortcut-key [key]
  (let [result (if (coll? key)
                 (string/join "+" key)
                 (case (if (string? key)
                         (string/lower-case key)
                         key)
                   ("cmd" "command" "mod" "⌘") (if mac? "⌘" "Ctrl")
                   ("meta") (if mac? "⌘" "⊞")
                   ("return" "enter" "⏎") "⏎"
                   ("shift" "⇧") "⇧"
                   ("alt" "option" "opt" "⌥") (if mac? "Opt" "Alt")
                   ("ctrl" "control" "⌃") "Ctrl"
                   ("space" " ") "Space"
                   ("up" "↑") "↑"
                   ("down" "↓") "↓"
                   ("left" "←") "←"
                   ("right" "→") "→"
                   ("tab") "Tab"
                   ("open-square-bracket") "["
                   ("close-square-bracket") "]"
                   ("dash") "-"
                   ("semicolon") ";"
                   ("equals") "="
                   ("single-quote") "'"
                   ("backslash") "\\"
                   ("comma") ","
                   ("period") "."
                   ("slash") "/"
                   ("grave-accent") "`"
                   ("page-up") ""
                   ("page-down") ""
                   (nil) ""
                   (name key)))]
    (if (= (count result) 1)
      result
      (string/capitalize result))))

(defn to-string [input]
  (cond
    (string? input) input
    (keyword? input) (name input)
    (symbol? input) (name input)
    (number? input) (str input)
    (uuid? input) (str input)
    (nil? input) ""
    :else (pr-str input)))

(defn- parse-shortcuts
  [s]
  (->> (string/split s #" \| ")
       (map (fn [x]
              (->> (string/split x #" ")
                   (map #(if (string/includes? % "+")
                           (string/split % #"\+")
                           %)))))))

(rum/defc part
  [ks size]
  (let [tiles (map print-shortcut-key ks)]
    (ui/button {:variant     :default
                :class       "bg-gray-03 text-gray-12 px-1.5 py-0 leading-4 h-5 hover:bg-gray-04 active:bg-gray-03 hover:text-gray-11"
                :interactive false
                :size        size}
      (for [[index tile] (map-indexed vector tiles)]
        [:<>
         (when (< 0 index)
           [:div.ui__button__tile-separator])
         [:div.ui__button__tile tile]]))))

(rum/defc root
  [shortcut & {:keys [size theme]
                       :or   {size  :xs
                              theme :gray}}]
  (when (seq shortcut)
    (let [shortcuts (if (coll? shortcut)
                      [shortcut]
                      (parse-shortcuts shortcut))]
      (for [[index binding] (map-indexed vector shortcuts)]
        [:<>
         (when (< 0 index)
           [:div.text-gray-11.text-sm "|"])
         (if (coll? (first binding))   ; + included
           (for [ks binding]
             (part ks size))
           (part binding size))]))))
