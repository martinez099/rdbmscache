# rdbms
This shows Redis as a cache for a Postgres database.

It uses lettuce to drive Redis and ormlite to map objects to SQL tables.

## Prerequisites

- Java 1.8
- Maven 3
- Docker 18

## Setup

- ```docker-compose up```
- ```mvn clean compile```

## Test

- ```mvn test```

## Run

- ```mvn exec:java -Dexec.mainClass="com.redislabs.demo.rdbms.Main" -Dexec.classpathScope=runtime```

REST-API running on http://localhost:4567

## Stop

- ```docker-compose down```
