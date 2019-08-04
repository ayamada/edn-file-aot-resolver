(ns edn-file-aot-resolver.core-test
  (:require [clojure.test :refer :all]
            [edn-file-aot-resolver.core :as efar]))

(deftest efar-test
  (testing "efar/defpath"
    (efar/defpath :path/dev.cljs.edn
      "test-resources/dev.cljs.edn")
    (is (= (efar/get :path/dev.cljs.edn :main)
           'foo.core))
    (is (thrown? clojure.lang.Compiler$CompilerException
                 (eval '(edn-file-aot-resolver.core/defpath :path/dev.cljs.edn
                          "another-file"))))
    )
  (testing "efar/get"
    (is (= (efar/get "test-resources/dev.cljs.edn" :main)
           'foo.core))
    (is (= (efar/get "test-resources/dev.cljs.edn" :output-to)
           "cljs-out/cl.js"))
    (is (= (efar/get "test-resources/dev.cljs.edn" :abc)
           nil))
    (is (= (efar/get "test-resources/dev.cljs.edn" :abc
                     "fallback")
           "fallback"))
    )
  (testing "efar/get-in"
    (is (= (efar/get-in "test-resources/dev.cljs.edn"
                        [:foreign-libs 0 :file])
           "js/pixi.min.js"))
    (is (= (efar/get-in "test-resources/dev.cljs.edn"
                        [:foreign-libs 9 :file]
                        :fallback)
           :fallback))
    (is (= (efar/get-in "test-resources/dev.cljs.edn" [:externs 1])
           "externs/lz-string.js"))
    )
  (testing "efar/get-meta"
    (is (= (efar/get-meta "test-resources/dev.cljs.edn" :watch-dirs)
           ["src" "src-dev"]))
    (is (= (efar/get-meta "test-resources/dev.cljs.edn" :foo
                          'fall-back)
           'fall-back))
    )
  (testing "efar/get-in-meta"
    (is (= (efar/get-in-meta "test-resources/dev.cljs.edn" [:watch-dirs 1])
           "src-dev"))
    (is (= (efar/get-in-meta "test-resources/dev.cljs.edn" [:foo :bar]
                             'fall-back)
           'fall-back))
    )
  (testing "invalid files"
    (is (thrown? clojure.lang.Compiler$CompilerException
                 (eval '(edn-file-aot-resolver.core/get "not-exists-file" 1))))
    (is (thrown? clojure.lang.Compiler$CompilerException
                 (eval '(edn-file-aot-resolver.core/get "README.md" :foo))))
    )
  )

