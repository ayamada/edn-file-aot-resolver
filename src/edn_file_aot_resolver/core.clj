(ns edn-file-aot-resolver.core
  (:refer-clojure :exclude [get get-in])
  (:require [clojure.java.io :as io]))

;;; TODO: Cache contents of files safely
;;;       (DO NOT IMPORT CACHE DATA TO BUILT FILES !!!)


(def ^:private global-path-table (atom {}))

(defmacro defpath [k path]
  (assert (keyword? k))
  (assert (string? path))
  (if-let [p (@global-path-table k)]
    (println (str "Warning: already registered key "
                  (pr-str k)
                  " as "
                  (pr-str p)
                  " , skipped."))
    (swap! global-path-table assoc k path))
  nil)


(defn- read-from-edn-file [path]
  (assert (.exists (io/file path)))
  (let [data-src (slurp path)
        data (when data-src
               (read-string data-src))]
    data))



(def ^:private not-found-mark (gensym))


;;; clojure.core/get-in can get from vec by number,
;;; but does NOT to get from seq by number!
;;; Need to get (get-in {:a '(9 8 7)} [:a 1]) ; => 8
(defn- get-in* [m ks & [not-found]]
  (loop [m m
         ks ks]
    (if (empty? ks)
      m
      (let [k (first ks)
            m2 (clojure.core/get m k not-found-mark)]
        (if-not (identical? m2 not-found-mark)
          (recur m2 (rest ks))
          (if-not (and (seq? m) (number? k))
            not-found
            (let [m2-again (nth m k not-found-mark)]
              (if-not (identical? m2-again not-found-mark)
                (recur m2-again (rest ks))
                not-found))))))))




(defn- immediate-value? [v]
  (cond
    (nil? v) true
    (true? v) true
    (false? v) true
    (keyword? v) true
    (string? v) true
    (number? v) true
    (symbol? v) false
    (vector? v) (every? immediate-value? v)
    (set? v) (every? immediate-value? v)
    (coll? v) false
    :else true))

(defn- unfold-quote [v]
  (when (coll? v)
    (when (= (first v) 'quote)
      (second v))))

(defn- resolve-macro-argument [a]
  (if-let [unquoted-value (unfold-quote a)]
    unquoted-value
    (do
      (assert (immediate-value? a))
      a)))



(defn- get-in-from-edn-file-internal [path ks else meta?]
  (assert (coll? ks) "ks must be collection")
  (assert (not (empty? ks)) "must need at least one key")
  (let [path (if (keyword? path)
               (@global-path-table path)
               path)
        _ (assert (string? path) "path must be string or defpath keyword")
        data (read-from-edn-file path)
        ks-resolved (mapv resolve-macro-argument ks)
        target (if meta?
                 (meta data)
                 data)
        r (get-in* target ks-resolved not-found-mark)]
    (if (= not-found-mark r)
      else
      (list 'quote r))))


(defmacro get-in [path ks & [else]]
  (get-in-from-edn-file-internal path ks else false))

(defmacro get-in-meta [path ks & [else]]
  (get-in-from-edn-file-internal path ks else true))


(defmacro get [path k & [else]]
  `(get-in ~path [~k] ~else))

(defmacro get-meta [path k & [else]]
  `(get-in-meta ~path [~k] ~else))














