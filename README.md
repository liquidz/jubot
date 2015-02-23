# jubot
[![Circle CI](https://circleci.com/gh/liquidz/jubot.svg?style=svg)](https://circleci.com/gh/liquidz/jubot) [![Dependency Status](https://www.versioneye.com/user/projects/54ca4610de7924f81a0000dc/badge.svg?style=flat)](https://www.versioneye.com/user/projects/54ca4610de7924f81a0000dc)


Chatbot framework in Clojure.

Jubot supports following adapters and brains:

 * adapter
  * Slack
 * brain
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

[![Deploy](https://www.herokucdn.com/deploy/button.png)](https://heroku.com/deploy?template=https://github.com/liquidz/jubot-sample)

WORK IN PROGRESS
[API documents](http://liquidz.github.io/jubot/api/)

## License

Copyright (C) 2015 [uochan](http://twitter.com/uochan)

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
