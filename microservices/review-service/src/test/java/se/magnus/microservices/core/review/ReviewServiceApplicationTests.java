package se.magnus.microservices.core.review;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class ReviewServiceApplicationTests extends MySqlTestBase {
	
}