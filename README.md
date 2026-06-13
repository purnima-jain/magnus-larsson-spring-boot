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


