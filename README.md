# fb-graph-api

It's Facebook Graph API that is based on clojure httpkit library. 


##Leiningen

###NOTE: this library is fully tested under Clojure 1.7

Just add the following to your project.clj file in the _dependencies_ section:

```
[fb-graph-api "0.1.0"]
```

## Usage

### Obtaining tokens

Facebook Graph API use two kinds of tokens: Application token and user token.

For obtain application token you can use function
```
(oauth/request-app-only-token app-id app-secret)
```
Obtaining user access token is three-step process.

Generate URL where Facebook will return temporary code:
```
(oauth/login-url app-id return-uri)
```

Fetch `code` parameter when user will be redirected to `return-url` and generate token:
```
(oauth/access-token app-id app-secret redirect-uri code)
```
(Optional) Exchange short-term token for long-term
```
(oauth/exchange-access-token app-id app-secret old-token)
```

## License

Copyright © 2015 Aliaksandr Barouski

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later
version.
