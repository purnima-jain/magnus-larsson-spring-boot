# magnus-larsson-spring-boot
Spring Boot project from Magnus Larsson Spring Boot book

The project consists of three core microservices, the **Product**, **Review**, and **Recommendation** services, all of which deal with one type of resource, and a composite microservice called the **Product Composite service**, which aggregates information from the three core services.

---

To track which containers actually responded to our requests, a `serviceAddress` attribute has been added to all responses, formatted as `hostname/ip-address:port`.

---

To build all the microsevices, cd to `microservices` folder and run `mvn clean install`.

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

