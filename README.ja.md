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


## Getting started

```sh
$ lein new jubot YOUR_JUBOT_PROJECT
$ cd YOUR_JUBOT_PROJECT
$ lein repl
user=> (in "jubot ping")
```

## ハンドラーの作成
### 書き方
 * 実例
 * 引数
  * :text
  * :user
 * docstring

### どこに書けばいいか
 * どのハンドラーが自動収集されるか
  * どこで対象のnsが定義されるか
  * xxx-handler が対象
  * xxx-test というnsは対象外

## スケジュールの作成
### スケジュールの書き方
 * 実例
 * 引数
  * なし
 * 実行タイミングのフォーマット
  * cronj

### どこに書けばいいか
 * どのスケジュールが自動的に収集されるか
  * xxx-schedule
  * ns はハンドラーと同じ

## 動作確認
### repl での動作確認
 * start, stop, restart
 * in 関数の使い方
  * in の定義場所(user.clj)

## デプロイ
### adapter, brain の指定方法
 * コマンドライン引数

### heroku へのデプロイ方法
 * asleep の回避方法
  * keep_awake
 * slack
  * 必要な環境変数
