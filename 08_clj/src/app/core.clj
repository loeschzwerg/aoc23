(ns app.core
  (:require
    [clojure.java.io :as io]
    [clojure.edn :as edn]
    [clojure.string :as str])
  (:gen-class))

(def file0 "8.txt")

(def data0
  (with-open [rdr (io/reader (io/resource file0))]
    (into [] (line-seq rdr))))

(def instructions
  (->> data0
       first
       seq
       (map {\L first
             \R second})))

(defn- parser [m s]
  (let [[k v] (str/split s #"=")
        k     (edn/read-string k) ; create key symbol
        v     (edn/read-string v)] ; create value tuple of symbols
    (assoc m k v)))

(def mapping (->> data0
                  rest
                  rest
                  (reduce parser {})))

(def first-state 'AAA)
(def final-state 'ZZZ)

(defn- traverse-path [start nav]
  (let [this      (atom start)
        decisions (atom nav)]
    (fn []
      (let [decide (first @decisions)]
        (swap! decisions rest)
        (reset! this (decide (mapping @this)))))))

(defn -main [& args]
  ; Traverse paths lazily
  (prn 'Steps 'to final-state
       (-> (repeatedly (traverse-path first-state (cycle instructions)))
           (.indexOf final-state)
           inc)))
