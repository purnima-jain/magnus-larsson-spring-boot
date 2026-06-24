package se.magnus.microservices.core.recommendation;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class RecommendationServiceApplicationTests extends MongoDbTestBase {

}
