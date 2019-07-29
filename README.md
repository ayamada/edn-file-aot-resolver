

*** Work in progress ***

*** 作成途中 ***


TODO: Add tests



# edn-file-aot-resolver

Refer a part of edn-file in local fs AoT, embed as immediate value

ednファイルの一部をコンパイル時に参照し、即値として埋め込む

This library aims to embed configurable parameters to built cljs code, mainly.

主に、cljsコードに設定値を埋め込む用途を想定しています。


## Install

[![Clojars Project](http://clojars.org/jp.ne.tir/edn-file-aot-resolver/latest-version.svg)](http://clojars.org/jp.ne.tir/edn-file-aot-resolver)


## Usage

`$ cat dev.cljs.edn`

```clojure
^{:watch-dirs ["src" "src-dev"]
  :css-dirs ["resources/public/css"]}
{:main foo.core
 :output-to "cljs-out/cl.js"
 :foreign-libs [{:file "js/pixi.min.js" :provides ["PIXI"]}
                {:file "js/lz-string.min.js" :provides ["LZString"]}]
 :externs ["externs/pixi.js" "externs/lz-string.js"]}
```

for clj:

```clojure
(ns xxx.yyy
  (:require [edn-file-aot-resolver.core :as efar]))

(efar/get "./dev.cljs.edn" :main) ; => 'foo.core

(efar/get "./dev.cljs.edn" :output-to) ; => "cljs-out/cl.js"

(efar/get "./dev.cljs.edn" :abc) ; => nil

(efar/get "./dev.cljs.edn" :abc "fallback") ; => "fallback"

(efar/get-in "./dev.cljs.edn" [:foreign-libs 0 :file]) ; => "js/pixi.min.js"

(efar/get-in "./dev.cljs.edn" [:foreign-libs 9 :file] :fallback) ; => :fallback

(efar/get-in "./dev.cljs.edn" [:externs 1]) ; => "externs/lz-string.js"

(efar/get-meta "./dev.cljs.edn" :watch-dirs) ; => ["src" "src-dev"]

(efar/get-in-meta "./dev.cljs.edn" [:watch-dirs 1]) ; => "src-dev"
```

for cljs:

```clojure
(ns xxx.yyy
  (:require-macros [edn-file-aot-resolver.core :as efar]))

;;; Same as for clj
```


## Notice

- `efar/get`, `efar/get-in`, `efar/get-meta` and `efar/get-in-meta` are macros.
  These are replaced to actual values in compile time.
  These values are independed from original files.
  - If you changed to referred entries in original files,
    you may remove `target/` for clean old values in compiled files.

- `efar/get` `efar/get-in` `efar/get-meta` `efar/get-in-meta` はマクロです。
  コンパイル時に実際の値に置換され、
  その後は参照元のファイルがなくても機能します。
  - コンパイル時埋め込みである為、もし元ファイル内のエントリの値を
    変更した際には一旦 `target/` 配下を破棄し、古い値が埋め込まれた
    コンパイル後ファイルを明示的に削除した方がよいでしょう。



## ChangeLog

- 0.1.0-SNAPSHOT (20XX-XX-XX)
    - Initial release (not yet)





