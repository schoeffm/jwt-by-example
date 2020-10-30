# JWT by example

Basically this is a self-study repo in order to get familiar with the OAuth2 Authentication Code Flow.

I don't use artificial tokens in this setup since one of the goals was to get familiar with how the actual flow is executed. I setup a `docker-compose.yml`-file which provides the necessary infrastructure.

- KeyCloak: simulating the authentication provider
- MockServer: used to simulate the callback the auth-provider calls after succesful login
- MicroProfile based Service (Payara and/or Quarkus (separate branch)): A microservice whose REST-endpoints are secured and expect a valid JWT

## Step 1: Build and Start the Stack

We'll have to do some manual setup in Keycloak and for that we'll have to start everything up

```bash
./buildAndRun.sh
```

## Step 2: Configure KeyCloak

We use a containerized keycloak as auth and identity provider. On startup it'll import a preconfigured realm - the only thing you'd have to provide is a concrete user.

Head over to http://localhost:8081/auth/admin/ and login as `admin`/`admin`:
1. go to _Users_ and click _Add user_ (left upper corner)
    - fill in the necessary data and click _Save_
2. click on the _Credentials_-Tab and enter a password for the new user
    - also untick the _Temporary_ toggle
3. click on the _Roles_-Tab and assign the single available role called _stefan_

That's it - as already mentioned, an import-script already created the followin' things for you:
- a _Realm_ called `intranetb2x`
    - on a realm level we decided to have a 30min lifespan for the access-token
- a _Client_ called `myclient` 
    - for this client we defined a callback-URI: `http://localhost:8082/callback` (which points to our containerized mockserver)
    - for that client no _Implicit Flow_ is available but we use the _Authorization Code Flow_.

## Step 3: Let's login

For that purpose - open an private tab in your browser and open:

http://localhost:8081/auth/realms/intranetb2x/protocol/openid-connect/auth/?client_id=myclient&response_type=code&state=fj8o3n7bdy1op5&scope=microprofile-jwt%20profile

Notice:
- the URI contains the realm we'd like to use
- it specifies the client we're pretending to be - here `myclient`
- we'd like to execute the _Authorization Code Flow_ - hence we pass in `code` as `response_type`
- finally, keycloak explicitly defines a `microprofile-jwt`-scope which later results in a set of claims in the token that allows for parsed by the MicroProfile implementation. `profile` is used in addition to get user-specific information as well.

You browser should present a login-page server by keycloak where you can leverage the user you've created during the previous step. After hitting _Login_ you' have to hurry a bit ...

## Step 4: Receiving and using the Auth Code

In keycloak `myclient` defined a callback-URI (`http://localhost:8082/callback`) which points to our mockserver. So after successful login - keycloak will redirect your browser to that mockserver.

Tail the logs of the mockserver in order to see what the auth-provider put into the callback-URI as parameter. You should find sth. like this:
```
"code" : [ "b97407f0-6cf1-4d70-9bf0-48e7902516e3.cf86c4ab-fa69-442a-a03e-8efefe459412.e76071c5-3da4-47f1-8b77-128c2e3d7656" ],
```
which is the Authorization Code that was generated and send back to the calling service.

## Step 4: Get the Access Token

Now we have to hurry - the Authorization Code is a short-lived token which expires quickly. The microservice would have to use this code to get the final _Access Token_. In our keycloak-setup this means a request like this: 

```
curl --location --request POST 'http://localhost:8081/auth/realms/intranetb2x/protocol/openid-connect/token' \
    --header 'Content-Type: application/x-www-form-urlencoded' \
    --data-urlencode 'grant_type=authorization_code' \
    --data-urlencode 'client_id=myclient' \
    --data-urlencode "code=<AUTHORIZATION CODE GOES IN HERE>" \
    --data-urlencode 'redirect_uri=http://localhost:8082/callback'
```

The result of that step should be a JSON-structure where one prop is the actual `access_token`. Voilá!

## Alternatively

Once you internalized how the auth-code flow works you can open http://localhost:8080/index.html in your browser. When hitting the button the same procedure starts, this time integrated. So, without a cookie in the browser you'd be redirected to the auth-providers login-page. After logging in the redirection lands at the java-backend again where the auth-code is exchanged for an access-token - that token is finally sent to the UI.

**Notice**: _this is just a demo - never pass the token over a non-encrypted connection!_
