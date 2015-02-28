# jubot
[![Circle CI](https://circleci.com/gh/liquidz/jubot.svg?style=svg)](https://circleci.com/gh/liquidz/jubot) [![Dependency Status](https://www.versioneye.com/user/projects/54ca4610de7924f81a0000dc/badge.svg?style=flat)](https://www.versioneye.com/user/projects/54ca4610de7924f81a0000dc)

**[API Docs](http://liquidz.github.io/jubot/api/)**

![jubot](resources/jubot.png)
**Chatbot framework in Clojure.**

Currently, jubot supports following adapters and brains:

 * Adapter
  * [Slack](https://slack.com/)
 * Brain
  * [Redis](http://redis.io/)

## Why jubot?

 * Simplicity
  * Handlers are simple functions, and these are **TESTABLE**.
 * Efficiency
  * Supports REPL friendly development that you love.
 * Extensibility
  * Easy to exntend system because jubot uses [stuartsierra/component](https://github.com/stuartsierra/component) as a component system.


## Getting Started

```sh
$ lein new jubot YOUR_JUBOT_PROJECT
$ cd YOUR_JUBOT_PROJECT
$ lein repl
user=> (in "jubot help")
```


## Handlers

Handler is a function to process user input.

### Ping pong example:
```clj
(defn ping-handler
  "jubot ping - reply with 'pong'"
  [{text :text}]
  (if (= "ping" text) "pong"))
```
 * Arguments
  * `:username`: User name
  * `:text`: Input string.
 * Document string
  * Document string will be shown in chatbot help.
```clj
user=> (in "jubot help")
```

Or you can use [`handler/regexp`](http://liquidz.github.io/jubot/api/jubot.handler.html#var-regexp):

```
(ns foo.bar
  (:require
    [jubot.handler :as jh]))

(jh/regexp
  #"^ping$" (constantly "pong"))
```

### Which handlers are collected automatically

Developers do not need to specify which handlers are used, because jubot collects handler functions automatically.

 * Public functions that matches `/^.*-handler$/` in `ns-prefix` will be collected automatically.
  * `ns-prefix` is a namespace regular expression. It is defined in `YOUR_JUBOT_PROJECT.core`.
  * However, namespaces that matches `/^.*-test$/` is excluded.


## Schedules
Schedule is a function that is called periodically as a cron.

### Good morning/night example:
```clj
(ns foo.bar
  (:require
    [jubot.scheduler :as js]))

(def good-morning-schedule
  (js/schedules
    "0 0 7 * * * *"  (fn [] "good morning")
    "0 0 21 * * * *" (fn [] "good night")))
```
 * Use [`scheduler/schedule`](http://liquidz.github.io/jubot/api/jubot.scheduler.html#var-schedule) or [`scheduler/schedules`](http://liquidz.github.io/jubot/api/jubot.scheduler.html#var-schedules) to define one or more schedules.
  * If the function returns string, jubot sends the string to adapter as a message. In other words, jubot does nothing when the function returns other than string.
 * Scheduling format
  * Jubot uses [cronj](https://github.com/zcaudate/cronj) for scheduling tasks, and scheduling format's details is here: [cronj format](http://docs.caudate.me/cronj/#crontab)

### Which schedules are collected automatically
As same as handler section, jubot collects schedule functions automatically.
 * Public schedule funtion that matches `/^.*-schedule$/` in `ns-prefix` will be collected automatically.
 * Test namespaces that matches `/^.*-test$/` are excluded.

## Development in REPL
Jubot provides some useful funcition to develop chatbot in REPL efficiently.
These functions are defined in `dev/user.clj`.
```clj
user=> (start)   ; start jubot system
user=> (stop)    ; stop jubot system
user=> (restart) ; reload sources and restart jubot system
user=> (in "jubot ping")
```

## Command line arguments

### Adapter name: `-a`, `--adapter`
 * Default value is "slack"
 * Possible adapter names are as follows:
  * slack
  * repl (for development)

### Brain name: `-b`, `--brain`
 * Default value is "memory"
 * Possible brain names are as follow:
  * redis
  * memory (for development)

### Chatbot name: `-n`, `--name`
 * Default value is "jubot"

## Deploy to Heroku
 1. Edit `Procfile` as you want
 1. Create and deploy (the following sample uses Redis as a brain)
```sh
heroku apps:create
heroku addons:add rediscloud
git push heroku master
```

Or use following deployment button based on [jubot-sample](https://github.com/liquidz/jubot-sample).

[![Deploy](https://www.herokucdn.com/deploy/button.png)](https://heroku.com/deploy?template=https://github.com/liquidz/jubot-sample)

### Slack setting
 * Required integration
  * Outgoing WebHooks
  * Incoming WebHooks
 * Required environmental variables
```sh
heroku config:add SLACK_OUTGOING_TOKEN="aaa"
heroku config:add SLACK_INCOMING_URL="bbb"
```

### Advanced setting for heroku
 * Avoid sleeping app
```sh
heroku config:add AWAKE_URL="Application url on heroku"
```

## License

Copyright (C) 2015 [uochan](http://twitter.com/uochan)

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.