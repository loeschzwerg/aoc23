(ns app.core
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.string :as string]
            [typed.clojure :as t]
            [clojure.core.typed :as ct])
  (:import (java.io BufferedReader)
           (java.util.regex Pattern)))


;; use file from resource
(t/ann file0 t/Str)
(def file0 "1.txt")

(t/ann clojure.core/line-seq [BufferedReader :-> (t/Vec t/Str)])

(t/ann data0 (t/Vec t/Str))
(def data0
  (with-open [rdr (io/reader (io/resource file0))]
    (into [] (line-seq rdr))))

(t/ann task1 [(t/Vec t/Str) :-> t/Int])
(defn- task1 [lines]
  (reduce +
          0
          (for [line lines]
            ((comp #(Integer/parseInt %)
                   (partial apply str)
                   (juxt first last)
                   (partial filter #(Character/isDigit ^Character %)))
             line))))

(t/ann dictionary (t/Map t/Str t/Str))
(def dictionary
  {"one" "1"
   "two" "2"
   "three" "3"
   "four" "4"
   "five" "5"
   "six" "6"
   "seven" "7"
   "eight" "8"
   "nine" "9"})

(t/ann dict-re Pattern)
(def dict-re
  (->> dictionary
       (keys)
       (interpose "|")
       (apply str)
       (format "(%s)")
       (re-pattern)))

(t/ann words->digits [t/Str -> t/Str])
(defn- words->digits [s]
  (prn (re-seq dict-re s) s)
  (if-let [matches (not-empty
                     (flatten (re-seq dict-re s)))]
    (-> (do (prn 'happy (into [] matches)) s)
        (str/replace-first (re-pattern (first matches)) (dictionary (first matches)))
        (str/replace (re-pattern (last matches)) (dictionary (last matches))))
    s))

(t/ann task2 [(t/Vec t/Str) :-> t/Int])
(defn- task2 [lines]
  (->> lines
       (map (fn [x] (doto (words->digits x) prn)))
       task1))



(defn -main []
  #_(prn (task1 data0))
  (prn (task2 data0)))
