# I. Building and running the application.

## 0. Prerequisites:
   JDK 21+
   Git

## 1. Clone repository
git clone https://github.com/stach78/LinkShorteningService.git
cd LinkShorteningService

## 2. run unit tests:
   ./mvnw test

## 3. run application:
   ./mvnw spring-boot:run

## 3a. Alternatively, build jar and run jar:
./mvnw clean package
java -jar target/*.jar --server.port=8080 --app.base-url=http://localhost:8080

# Ia. Building and running the application using docker

## 0. Prerequisites:
   docker (version 28.3.3 or newer)

## 1. Build docker image:
   docker build -t lss .

## 2. Run docker image:
   docker run --rm -p 8080:8080 -e APP_BASE_URL=http://localhost:8080 --name lss lss

# II. Testing the application

Test using curl, e. g.:

## 1. adding user
curl -s -X POST http://localhost:8080/api/users/register -H 'Content-Type: application/json' -d '{"username":"user","password":"password1"}'

## 2. creating short urls
curl -i -X POST http://localhost:8080/shorten -H 'Content-Type: application/json' -d '{"url":"https://onet.pl"}' -u user:password1
curl -i -X POST http://localhost:8080/shorten -H 'Content-Type: application/json' -d '{"url":"https://onet.pl","customShortUrl":"onet"}' -u user:password1

Paste url returned from above command into browser to see working redirect.

## 3. checking short url metrics
curl -i -X GET http://localhost:8080/api/metrics/onet -u user:password1

## 4. browsing all short urls of a given user
curl -i -X GET http://localhost:8080/api/user/links?page=0 -u user:password1

## 5. delete short url
curl -i -X DELETE http://localhost:8080/api/user/links/onet -u user:password1

## 6. delete user
curl -s -X DELETE http://localhost:8080/api/users/delete_user -H 'Content-Type: application/json' -u user:password1

# III. API documentation

API documentation can be accessed by starting the application and navigating to:
http://localhost:8080/swagger-ui/index.html
http://localhost:8080/v3/api-docs

# IV. Libraries used
Spring Boot

H2 database

springdoc