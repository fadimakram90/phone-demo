# Phone Demo App
### International Phone Number Validator

## Backend Technologies:
- Spring Boot
- SQLite DB

## Frontend Technologies:
- React JS
- Web Server: nginx

## Main Features:
- Filter by country and phone validation status
- Pagination

## Backend Unit Tests:
- Find all unit tests in ```com.demo.phone.PhoneDemoApplicationTests```

## Ports Used: 
- Backend Port: 8080
- HTTP Port: 80

## Software prerequisites for booting up the project:
- Java 8
- Maven
- Docker

## Boot up the project:
```sh
mvn clean install
docker-compose build
docker-compose up
```
Then go to: http://localhost