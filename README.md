

*** Work in progress ***

*** 作成途中 ***


# edn-file-aot-resolver

Refer a part of edn-file AoT, embed as immediate value

ednファイルの一部をコンパイル時に参照し、即値として埋め込む


## Install

[![Clojars Project](http://clojars.org/jp.ne.tir/edn-file-aot-resolver/latest-version.svg)](http://clojars.org/jp.ne.tir/edn-file-aot-resolver)


## Usage

```clojure
$ cat dev.cljs.edn
^{:watch-dirs ["src" "src-dev"]
  :css-dirs ["resources/public/css"]}
{:main foo.core
 :output-to "cljs-out/cl.js"
 :ring-server-options {:host "0.0.0.0"
                       :port 9500}
 }
{:deps {org.clojure/clojure {:mvn/version "1.10.1"}
        org.clojure/clojurescript {:mvn/version "1.10.520"}
        com.bhauman/figwheel-main {:mvn/version "0.2.1"}
        com.bhauman/rebel-readline-cljs {:mvn/version "0.1.4"}}
 :paths ["src" "target" "resources"]}
```

```clojure
(ns xxx.yyy
  (:require [edn-file-aot-resolver.core :as efar]))

(efar/get :paths) ; => ["src" "target" "resources"]

(efar/get :abc) ; => nil

(efar/get :abc "fallback") ; => "fallback"

(efar/get-in [:deps 'org.clojure/clojure :mvn/version]) ; => "1.10.1"

(efar/get-in [:deps 'com.example/foo :mvn/version] :fallback) ; => :fallback

(efar/get-in [:paths 1]) ; => "target"

(efar/get-meta :main) ; => 'foo.core

(efar/get-in-meta [:ring-server-options :port]) ; => 9500

```

for cljs:

```clojure
(ns xxx.yyy
  (:require-macros [edn-file-aot-resolver.core :as efar]))

...
```


## Notice

- `efar/get` and `efar/get-in` are macros.
  These are replaced to actual values in compile time.
  These values are NOT depend on `deps.edn` anymore.
  - If you changed to referred entries in `deps.edn`,
    you may remove `target/` for clean old values in compiled files.

- `efar/get` と `efar/get-in` はマクロです。
  コンパイル時に実際の値に置換され、
  その後は `deps.edn` がなくても機能します。
  - コンパイル時埋め込みである為、もし `deps.edn` 内の参照エントリの値を
    変更した際には一旦 `target/` 配下を破棄し、古い値が埋め込まれた
    コンパイル後ファイルを明示的に削除した方がよいでしょう。



## ChangeLog

- 0.1.0-SNAPSHOT (20XX-XX-XX)
    - Initial release (not yet)





