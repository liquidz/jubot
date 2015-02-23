# jubot

## jubotとは

clojure の chatbot フレームワーク

現状対応しているのは以下の通り

 * Adapter
  * Slack
  * repl (for development)
 * Brain
  * Redis
  * memory (for development)

## なぜjubotなのか

 * シンプル
  * ハンドラーがただの関数なのでシンプル
  * ただの関数はテストしやすい
 * 開発しやすい
  * repl を使った効率的な開発をサポート
 * 拡張性
  * component システムを使っているので拡張しやすい

## Getting Started

```sh
$ lein new jubot YOUR_JUBOT_PROJECT
$ cd YOUR_JUBOT_PROJECT
$ lein repl
user=> (in "jubot ping")
```

## ハンドラーの作成
### 書き方
 * "ping" という入力に対して "pong" を返す
```clj
(defn ping-handler
  "jubot ping - reply with 'pong'"
  [{text :text}]
  (if (= "ping" text) "pong"))
```
 * 引数
  * `:text`: 入力された文字列
  * `:username`: 入力したユーザー名
 * docstring
  * docstring はヘルプ表示に使われる
```sh
user=> (in "jubot help")
```

### どこに書けばいいか
 * どのハンドラーが自動収集されるか
  * `YOUR_JUBOT_PROJECT.core` の `ns-prefix` にマッチする ns が対象
  * 名前が `^.*-handler$` にマッチするパブリックな関数
 * 例外として xxx-test という ns は `ns-prefix` にマッチしても対象外

## スケジュールの作成
### スケジュールの書き方
 * 毎日07:00におはよう、21:00におやすみという
```clj
(ns foo.bar
  (:require
    [jubot.adapter   :as ja]
    [jubot.scheduler :as js]))

(def good-morning-schedule
  (js/schedules
    "0 0 7 * * * *"  #(ja/out "good morning")
    "0 0 21 * * * *" #(ja/out "good night")))
```
 * 引数
  * なし
 * 実行タイミングのフォーマット
  * [cronj format](http://docs.caudate.me/cronj/#crontab)

### どこに書けばいいか
 * どのスケジュールが自動的に収集されるか
  * ハンドラーと同様に `YOUR_JUBOT_PROJECT.core` の `ns-prefix` にマッチするnsが対象
  * 名前が `^.*-schedule$` にマッチするパブリックな関数
 * xxx-test にマッチする ns が除外されるのはハンドラー同様

## 動作確認
### repl での動作確認
 * `(start)`: jubot を起動
 * `(stop)`: jubot を停止
 * `(restart)`: jubot を再起動。修正されたソースは再読み込みされる
 * in 関数
  * repl 上で bot にメッセージを送る特殊関数
  * dev/user.clj に定義しているので名前は自由に変更可能

### コマンドライン引数
 * `-a`, `--adapter`: アダプター名 (デフォルト: slack)
  * `slack`: Slack アダプター
  * `repl`: 開発用 repl アダプター
 * `-b`, `--brain`: ブレイン名 (デフォルト: memory)
  * `redis`: Redis ブレイン
  * `memory`: 開発用 memory ブレイン
 * `-n`, `--name`: ボット名 (デフォルト: jubot)

## デプロイ

### heroku へのデプロイ方法

 * Procfile を必要に応じて編集
 * heroku へデプロイ
```
heroku apps:create
heroku addons:add rediscloud
git push heroku master
```
 * Slack
  * 必要な integration
   * Outgoing WebHooks
   * Incoming WebHooks
  * 必要な環境変数
```
heroku config:add SLACK_OUTGOING_TOKEN="aaa"
heroku config:add SLACK_INCOMING_URL="bbb"
```
 * asleep の回避
```
heroku config:add AWAKE_URL="Application url on heroku"
```
