# jubot
[![Circle CI](https://circleci.com/gh/liquidz/jubot.svg?style=svg)](https://circleci.com/gh/liquidz/jubot) [![Dependency Status](https://www.versioneye.com/user/projects/54ca4610de7924f81a0000dc/badge.svg?style=flat)](https://www.versioneye.com/user/projects/54ca4610de7924f81a0000dc)

Chatbot framework in Clojure.

Jubot supports following adapters and brains:

 * Adapter
  * Slack
 * Brain
  * Redis

## Why jubot?

 * Simplicity
  * Handler function is a simple function, and it is testable.
 * Efficiency
  * Jubot supports to develop a chatbot in REPL efficiently.
 * Extensibility
  * Easy to exntend system because Jubot uses [stuartsierra/component](https://github.com/stuartsierra/component).

## Getting Started

```sh
$ lein new jubot YOUR_JUBOT_PROJECT
$ cd YOUR_JUBOT_PROJECT
$ lein repl
user=> (in "jubot ping")
```

## Handler functions

Ping pong example:
```clj
(defn ping-handler
  "jubot ping - reply with 'pong'"
  [{text :text}]
  (if (= "ping" text) "pong"))
```
 * Arguments
  * `:username`: User name
  * `:text`: User inputted string.
 * Document string
  * Document string is used to show chatbot help.
```sh
user=> (in "jubot help")
```

### Which handlers are collected automatically

Developers do not need to specify which handlers are used, because jubot collect handler functions automatically.

 * Public functions which matches `^.*-handler$` in `ns-prefix` will be collected automatically.
  * `ns-prefix` is a namespace regular expression. It is defined in `YOUR_JUBOT_PROJECT.core`.
  * However, namespaces which matches `^.*-test$` is excluded.

## Schedules
Good morning/night example
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
 * Arguments
  * No arguments
 * Timing format
  * [cronj format](http://docs.caudate.me/cronj/#crontab)

### Which schedules are collected automatically




[![Deploy](https://www.herokucdn.com/deploy/button.png)](https://heroku.com/deploy?template=https://github.com/liquidz/jubot-sample)
[API documents](http://liquidz.github.io/jubot/api/)

## License

Copyright (C) 2015 [uochan](http://twitter.com/uochan)

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.