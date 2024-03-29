[![Build Status](https://travis-ci.org/ayamada/edn-file-aot-resolver.svg?branch=master)](https://travis-ci.org/ayamada/edn-file-aot-resolver)
[![Clojars Project](https://img.shields.io/clojars/v/jp.ne.tir/edn-file-aot-resolver.svg)](https://clojars.org/jp.ne.tir/edn-file-aot-resolver)
[![release version](https://img.shields.io/github/release/ayamada/edn-file-aot-resolver.svg)](https://github.com/ayamada/edn-file-aot-resolver/releases)
[![license](https://img.shields.io/github/license/ayamada/edn-file-aot-resolver.svg)](LICENSE)


# edn-file-aot-resolver

Refer a part of edn-file in local fs AoT, embed as immediate value

ednファイルの一部をコンパイル時に参照し、即値として埋め込む

This library aims to embed configurable parameters to built cljs code.

主に、cljsコードに設定値を埋め込む用途を想定しています。


## Install

- https://clojars.org/jp.ne.tir/edn-file-aot-resolver


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

for clj

```clojure
(ns xxx.yyy
  (:require [edn-file-aot-resolver.core :as efar]))

(efar/get "./dev.cljs.edn" :main) ; => 'foo.core

(efar/defpath ::dce "./dev.cljs.edn")

(efar/get ::dce :main) ; => 'foo.core

(efar/get ::dce :output-to) ; => "cljs-out/cl.js"

(efar/get ::dce :abc) ; => nil

(efar/get ::dce :abc "fallback") ; => "fallback"

(efar/get-in ::dce [:foreign-libs 0 :file]) ; => "js/pixi.min.js"

(efar/get-in ::dce [:foreign-libs 9 :file] :fallback) ; => :fallback

(efar/get-in ::dce [:externs 1]) ; => "externs/lz-string.js"

(efar/get-meta ::dce :watch-dirs) ; => ["src" "src-dev"]

(efar/get-in-meta ::dce [:watch-dirs 1]) ; => "src-dev"
```

for cljs

```clojure
(ns xxx.yyy
  (:require-macros [edn-file-aot-resolver.core :as efar]))

;;; Same as clj
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

- 1.0.1 (2019-08-04)
    - Don't throw exception by defpath overwriting, but println warnings

- 1.0.0 (2019-08-04)
    - Initial release





