netflix-example-project  
=========================

Sample Application for 

## Run Locally

```shell
git clone ...
./gradlew clean build
java -jar build/libs/netflix-example-project.war
open http://localhost:8888
```

### See Documentation
Swagger: `http://localhost:8888/swagger-ui.html`
AsciiDoc: `http://localhost:8888/docs/index.html` 

## Deploy on Heroku

1. Install the Heroku CLI

2. Deploy

```shell
heroku login
heroku create
git push heroku master
# wait until you see kind of:
# https://some-words-numbers.herokuapp.com/ deployed to Heroku
heroku open
```