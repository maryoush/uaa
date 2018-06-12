# Run the uaa as

./gradlew default run

Assumptions

* uaa runs on localhost
* uaa runs on port -> 8080
* uaa is mounted at /uaa
* have installed uaac


# Issue admin token key ${ADMIN TOKEN}

{code}
 curl 'http://localhost:8080/uaa/oauth/token' -i -X POST     -H 'Content-Type: application/x-www-form-urlencoded'     -H 'Accept: application/json'     -d 'client_id=admin&client_secret=adminsecret&grant_type=client_credentials&response_type=token&token_format=opaque'
{code}


# Create availability zone (AZ) (foo)

{code}
curl 'http://localhost:8080/uaa/identity-zones' -X POST     -H 'Content-Type: application/json'     -H 'Authorization: Bearer 687ddc3e2e524b3bb7c7259093283615'  -d @identity-zone-foo.json
{code}


# Get AZ specific token keys (no bearer token) (PUBKEY)

{code}
curl 'http://localhost:8080/uaa/token_keys'     -H 'Accept: application/json'     -H 'Host: foo.localhost'  | jq
{code}


Ok now client generation (using uuac)

{code}

//target
uaac target http://localhost:8080/uaa
//login as admin
uaac token client get admin -s adminsecret -t
{code}


## create foo client in AZ foo
{code}
//
uaac -t curl -H"X-Identity-Zone-Id:foo" -XPOST -H"Content-Type:application/json" -H"Accept:application/json" -d @foo-user.json  /oauth/clients
{code}


# Issue for JWT token for this client

{code}
curl http://localhost:8080/uaa/oauth/token -H"Host:foo.localhost" -u foo:foo -d 'grant_type=client_credentials' -H"content-type:application/x-www-form-urlencoded;charset=utf-8" | jq .
{code}

Token issued for "iss": "http://foo.localhost:8080/uaa/oauth/token", with  "kid": "fookey", signed with (PUBKEY, see above) see jwt-foo.txt


{code}
//header
{
  "alg": "RS256",
  "kid": "fookey",
  "typ": "JWT"
}

//payload
{
  "jti": "e57230a8b6af4ab482a4f3402b77e489",
  "ext_attr": {
    "foo": "bar"
  },
  "sub": "foo",
  "authorities": [
    "clients.read",
    "clients.secret",
    "clients.write",
    "uaa.admin",
    "clients.admin",
    "scim.write",
    "scim.read"
  ],
  "scope": [
    "clients.read",
    "clients.secret",
    "clients.write",
    "uaa.admin",
    "clients.admin",
    "scim.write",
    "scim.read"
  ],
  "client_id": "foo",
  "cid": "foo",
  "azp": "foo",
  "grant_type": "client_credentials",
  "rev_sig": "59c4f6d",
  "iat": 1528815092,
  "exp": 1528818692,
  "iss": "http://foo.localhost:8080/uaa/oauth/token",
  "zid": "foo",
  "aud": [
    "scim",
    "clients",
    "uaa",
    "foo"
  ]
}

{code}



