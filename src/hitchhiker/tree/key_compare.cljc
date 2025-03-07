(ns hitchhiker.tree.key-compare
  #?(:cljs (:refer-clojure :exclude [-compare]))
  (:require [hitchhiker.tree.node :as n])
  #?(:clj (:import (clojure.lang IPersistentVector
                                 IPersistentSet
                                 IPersistentMap
                                 Symbol
                                 Keyword))))

(defprotocol IKeyCompare
  (-compare [key1 key2]))

(defn throw-unsupported-type
  [t]
  (throw (ex-info (str "Type not supported:"
                       (type t))
                  {:value t})))

(extend-protocol n/IEDNOrderable
  #?@(:clj [IPersistentMap
            (-order-on-edn-types [_] 0)

            IPersistentVector
            (-order-on-edn-types [_] 1)

            IPersistentSet
            (-order-on-edn-types [_] 2)

            Number
            (-order-on-edn-types [_] 3)

            String
            (-order-on-edn-types [_] 4)

            Symbol
            (-order-on-edn-types [_] 5)

            Keyword
            (-order-on-edn-types [_] 6)

            Boolean
            (-order-on-edn-types [_] 7)

            nil
            (-order-on-edn-types [_] 10000)

            Object
            (-order-on-edn-types [t] (throw-unsupported-type t))]

      :cljs [cljs.core/IMap
             (-order-on-edn-types [_] 0)

             cljs.core/IVector
             (-order-on-edn-types [_] 1)

             cljs.core/ISet
             (-order-on-edn-types [_] 2)

             number
             (-order-on-edn-types [_] 3)

             string
             (-order-on-edn-types [_] 4)

             cljs.core/Symbol
             (-order-on-edn-types [_] 5)

             cljs.core/Keyword
             (-order-on-edn-types [_] 6)

             boolean
             (-order-on-edn-types [_] 7)

             nil
             (-order-on-edn-types [_] 10000)

             object
             (-order-on-edn-types [t] (throw-unsupported-type t))]))


(extend-protocol IKeyCompare
  ;; By default, we use the default comparator
  #?@(:clj
      [Object
       (-compare [key1 key2]
                 (if (or (= (class key1) (class key2))
                         (= (n/-order-on-edn-types key1)
                            (n/-order-on-edn-types key2)))
                   (try
                     (compare key1 key2)
                     (catch ClassCastException e
                       (- (n/-order-on-edn-types key2)
                          (n/-order-on-edn-types key1))))
                   (- (n/-order-on-edn-types key2)
                      (n/-order-on-edn-types key1))))
       String
       (-compare [^String key1 key2]
                 (if (instance? String key2)
                   (.compareTo key1 key2)
                   (try
                     (compare key1 key2)
                     (catch ClassCastException e
                       (- (n/-order-on-edn-types key2)
                          (n/-order-on-edn-types key1))))))
       Double
       (-compare [^Double key1 key2]
                 (if (instance? Double key2)
                   (.compareTo key1 key2)
                   (try
                     (compare key1 key2)
                     (catch ClassCastException e
                       (- (n/-order-on-edn-types key2)
                          (n/-order-on-edn-types key1))))))
       BigDecimal
       (-compare [^BigDecimal key1 key2]
                 (if (instance? BigDecimal key2)
                   (.compareTo key1 key2)
                   (try
                     (compare key1 key2)
                     (catch ClassCastException e
                       (- (n/-order-on-edn-types key2)
                          (n/-order-on-edn-types key1))))))
       Long
       (-compare [^Long key1 key2]
                 (if (instance? Long key2)
                   (.compareTo key1 key2)
                   (try
                     (compare key1 key2)
                     (catch ClassCastException e
                       (- (n/-order-on-edn-types key2)
                          (n/-order-on-edn-types key1))))))
       BigInteger
       (-compare [^BigInteger key1 key2]
                 (if (instance? BigInteger key2)
                   (.compareTo key1 key2)
                   (try
                     (compare key1 key2)
                     (catch ClassCastException e
                       (- (n/-order-on-edn-types key2)
                          (n/-order-on-edn-types key1))))))
       ]
      :cljs
      [number
       (-compare [key1 key2]
                 (if (or (= (type key1) (type key2))
                         (= (n/-order-on-edn-types key1)
                            (n/-order-on-edn-types key2)))
                   (try
                     (cljs.core/compare key1 key2)
                     (catch js/Error e
                       (- (n/-order-on-edn-types key2)
                          (n/-order-on-edn-types key1))))
                   (- (n/-order-on-edn-types key2)
                      (n/-order-on-edn-types key1))))
       object
       (-compare [key1 key2]
                 (if (or (= (type key1) (type key2))
                         (= (n/-order-on-edn-types key1)
                            (n/-order-on-edn-types key2)))
                   (try
                     (cljs.core/compare key1 key2)
                     (catch js/Error e
                       (- (n/-order-on-edn-types key2)
                          (n/-order-on-edn-types key1))))
                   (- (n/-order-on-edn-types key2)
                      (n/-order-on-edn-types key1))))]))
