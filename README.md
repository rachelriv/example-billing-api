Example Billing API  
=========================

## Run Locally

```shell
./gradlew clean build
java -jar build/libs/netflix-example-project.war
open http://localhost:8888
```

### See Documentation Locally
Swagger: http://localhost:8888/swagger-ui.html  
AsciiDoc: http://localhost:8888/docs/index.html 

## Deploy on Heroku

1. Install the Heroku CLI

2. Deploy

```shell
heroku login
heroku create
git push heroku master
# wait until you see:
# https://example-billing-api.herokuapp.com/ deployed to Heroku
heroku open
```

## Deployed Instance
http://example-billing-api.herokuapp.com/

### See Deployed Documentation
Swagger Docs: http://example-billing-api.herokuapp.com/swagger-ui.html  
AsciiDoc: https://example-billing-api.herokuapp.com/docs/index.html