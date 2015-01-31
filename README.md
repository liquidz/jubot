# jubot
[![Circle CI](https://circleci.com/gh/liquidz/jubot.svg?style=svg)](https://circleci.com/gh/liquidz/jubot) [![Dependency Status](https://www.versioneye.com/user/projects/54ca4610de7924f81a0000dc/badge.svg?style=flat)](https://www.versioneye.com/user/projects/54ca4610de7924f81a0000dc)

Clojure bot framework

## Usage

```
beco -i -c heroku /heroku_login.sh
beco heroku apps:create
beco heroku addons:add rediscloud
beco -c heroku git push heroku master
```

### Slack Adapter

```
beco heroku config:add SLACK_OUTGOING_TOKEN=YOUR_SLACK_OUTGOING_TOKEN
beco heroku config:add SLACK_INCOMING_URL=YOUR_SLACK_INCOMING_URL
```

## License

Copyright (C) 2015 [uochan](http://twitter.com/uochan)

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
