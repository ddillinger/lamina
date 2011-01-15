;;   Copyright (c) Zachary Tellman. All rights reserved.
;;   The use and distribution terms for this software are covered by the
;;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;;   which can be found in the file epl-v10.html at the root of this distribution.
;;   By using this software in any fashion, you are agreeing to be bound by
;;   the terms of this license.
;;   You must not remove this notice, or any other, from this software.

(ns lamina.core.fn
  (:use [lamina.core channel pipeline]))

(defn async [f]
  (fn [& args]
    (apply run-pipeline []
      (concat
	(map (fn [x] (read-merge (constantly x) conj)) args)
	[#(apply f %)]))))

(defmacro afn [& fn-args]
  `(let [f# (fn ~@fn-args)]
     (async f#)))

(defmacro future* [& body]
  `(let [result# (result-channel)]
     (future
       (try
	 (enqueue (:success result#) (do ~@body))
	 (catch Throwable t#
	   (enqueue (:error result#) [nil t#]))))
     result#))