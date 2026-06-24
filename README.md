# magnus-larsson-spring-boot
Spring Boot project from Magnus Larsson Spring Boot book

The project consists of three core microservices, the **Product**, **Review**, and **Recommendation** services, all of which deal with one type of resource, and a composite microservice called the **Product Composite service**, which aggregates information from the three core services.

---

To track which containers actually responded to our requests, a `serviceAddress` attribute has been added to all responses, formatted as `hostname/ip-address:port`.

---

To build `api` library:
```
cd api
mvn clean install
```

Similary, to build `util` library:
```
cd util
mvn clean install
```

To build all the microsevices, (but do not do this, it messes up the order. We want the three jars to be built before promduct-composite jar, and this command does not enforces the order):
```
cd microservices
mvn clean install
```

---

To start-up each of the microservice, cd to their folder and run `mvn spring-boot:run`.

---

The implementation of the composite services is divided into two parts: an integration component that handles the outgoing HTTP requests to the core services and the composite service implementation itself. The main reason for this division of responsibility is that it simplifies automated unit and integration testing; we can test the service implementation in isolation by replacing the integration component with a mock.

---

For the `getProduct()` implementation, the `getForObject()` method can be used in `RestTemplate`. The expected response is a `Product` object. It can be expressed in the call to `getForObject()` by specifying the `Product.class` class that `RestTemplate` will map the JSON response to.

For the calls to `getRecommendations()` and `getReviews()`, a more advanced method, `exchange()`, has to be used. The reason for this is the automatic mapping from a JSON response to a model class that `RestTemplate` performs. The `getRecommendations()` and `getReviews()` methods expect a generic list in the responses, that is, `List<Recommendation>` and `List<Review>`. Since generics don’t hold any type of information at runtime, we can’t specify that the methods expect a generic list in their responses. Instead, we can use a helper class from the Spring Framework, `ParameterizedTypeReference`, which is designed to resolve this problem by holding the type information at runtime. This means that `RestTemplate` can figure out what class to map the JSON responses to. To utilize this helper class, we have to use the more involved `exchange()` method instead of the simpler `getForObject()` method on `RestTemplate`.

---
It is also important to separate protocol-specific handling of errors, such as HTTP status codes, from the business logic.

I have created a set of Java exceptions in the `util` project that are used by both the API implementations and the API clients, initially `InvalidInputException` and `NotFoundException`.

To separate protocol-specific error handling from the business logic in the REST controllers, that is, the API implementations, I have created a utility class, `GlobalControllerExceptionHandler.java`, in the util project, which is annotated as `@RestControllerAdvice`.

For each Java exception that the API implementations throw, the utility class has an exception handler method that maps the Java exception to a proper HTTP response, that is, with a proper HTTP status and HTTP response body.

For example, if an API implementation class throws `InvalidInputException`, the utility class will map it to an HTTP response with the status code set to `422` (`UNPROCESSABLE_ENTITY`).

Whenever a REST controller throws any of these exceptions, Spring will use the utility class to create an HTTP response.

---

Error handling in API implementations

API implementations use the exceptions in the `util` project to signal errors. They will be reported back to the REST client as HTTPS status codes indicating what went wrong. For example, the `Product` microservice implementation class, `ProductServiceImpl.java`, uses the `InvalidInputException` exception to return an error that indicates invalid input, as well as the `NotFoundException` exception to tell us that the product that was asked for does not exist.

---

Error handling in the API client

The API client, that is, the integration component of the `Composite` microservice, does the reverse; it maps the `422` (`UNPROCESSABLE_ENTITY`) HTTP status code to `InvalidInputException` and the `404` (`NOT_FOUND`) HTTP status code to `NotFoundException`. See the `getProduct()` method in `ProductCompositeIntegration.java` for the implementation of this error-handling logic. 

The error handling for `getRecommendations()` and `getReviews()` in the integration component is a bit more relaxed – classed as best-effort, meaning that if it succeeds in getting product information but fails to get either recommendations or reviews, it is still considered to be okay. However, a warning is written to the log.

---

To create a Docker image:
```
cd product-service
docker build -t product-service .
```

To run this Docker image:
```
docker run -p7002:8080 -e "SPRING_PROFILES_ACTIVE=docker" --name product-service product-service
```

And this service is reachable at: http://localhost:7002/product/123

To get to the logs, the -f option tells the command to follow the log output:
```
docker logs <container-name> -f
```

To remove and stop the container, the -f option forces Docker to remove the container, even if it is running. Docker will automatically stop the container before it removes it.
```
docker rm -f <container-name>
```

---

To build all the images via docker-compose, go to the root folder where `docker-compose.yml` exists, and run
```
docker compose build
```

To start all the containers with a single command, `-d` is for detached mode
```
docker compose up -d
```

And now you can access product-composite at `http://localhost:8080/product-composite/123`

To bring everything down:

```
docker compose down
```

---

We are adding OpenAPI Specification only for the `product-composite-service` microservice because that is the only one which is externally accessible.

---

To browse the OpenAPI documentation, we will use the embedded Swagger UI viewer. If we open the http://localhost:8080/openapi/swagger-ui.html URL in a web browser.

It contains general information we specified in the springdoc-openapi OpenAPI bean and a link to the actual OpenAPI document (the json), /openapi/v3/api-docs, pointing to http://localhost:8080/openapi/v3/api-docs. Note that this is the link to the OpenAPI document that can be exported to an API gateway,

---

The `product` and `recommendation` microservices will use Spring Data for MongoDB and the `review` microservice will use Spring Data for the Java Persistence API (JPA) to access a MySQL database.

To access the databases manually, we will use the CLI tools provided in the Docker images used to run the databases. We will also expose the standard ports used for each database in Docker Compose, `3306` for MySQL, and `27017` for MongoDB. This will enable us to use our favorite database tools for accessing the databases in the same way as if they were running locally on our computers.

---

When we run MongoDB as a Docker container, we can run queries from the CLI like this:
```
docker compose exec mongodb mongosh product-db --quiet --eval "db.products.find()"
```

---

When we run MySQL as a Docker container, we can run queries from the CLI like this:
```
docker compose exec mysql mysql -uuser -p review-db -e "select * from reviews"
```

---

MapStruct, a Java bean mapping tool, makes it easy to transform between Spring Data entity objects and the API model classes.

---

The entity classes are similar to the corresponding API model classes in terms of what fields they contain. We will add two fields, `id` and `version`, in the entity classes compared to the API model classes.

**`id`** </br>
The `id` field is used to hold the database identity of each stored entity, corresponding to the primary key when using a relational database. </br>
We will delegate the responsibility of generating unique values for the `id` field to Spring Data. </br>
Depending on the database used, Spring Data can delegate this responsibility to the database engine or handle it on its own. In either case, the application code does not need to consider how a unique database `id` value is set. </br>
The `id` field is not exposed in the API, as a best practice from a security perspective. </br>
The fields in the model classes that identify an entity will be assigned a unique index in the corresponding entity class, to ensure consistency in the database from a business perspective.</br>

**`version`** </br>
The `version` field is used to implement optimistic locking, allowing Spring Data to verify that updates of an entity in the database do not overwrite a concurrent update. </br>
If the value of the `version` field stored in the database is higher than the value of the `version` field in an update request, this indicates that the update is performed on stale data—the information to be updated has been updated by someone else since it was read from the database. Attempts to perform updates based on stale data will be prevented by Spring Data.</br>

---

