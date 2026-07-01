package se.magnus.microservices.core.review;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.core.publisher.Mono;
import se.magnus.microservices.core.review.domain.model.Review;
import se.magnus.microservices.core.review.persistence.ReviewRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class ReviewServiceApplicationTests extends MySqlTestBase {

	@Autowired
	private WebTestClient client;

	@Autowired
	private ReviewRepository repository;

	@BeforeEach
	void setupDb() {
		repository.deleteAll();
	}

	@Test
	void getReviewsByProductId() {
		int productId = 1;

		assertEquals(0, repository.findByProductId(productId).size());

		postAndVerifyReview(productId, 1, HttpStatus.OK);
		postAndVerifyReview(productId, 2, HttpStatus.OK);
		postAndVerifyReview(productId, 3, HttpStatus.OK);

		assertEquals(3, repository.findByProductId(productId).size());

		getAndVerifyReviewsByProductId(productId, HttpStatus.OK)
				.jsonPath("$.length()").isEqualTo(3)
				.jsonPath("$[2].productId").isEqualTo(productId)
				.jsonPath("$[2].reviewId").isEqualTo(3);
	}
	
	private WebTestClient.BodyContentSpec postAndVerifyReview(int productId, int reviewId, HttpStatus expectedStatus) {
		Review review = new Review(productId, reviewId, "Author " + reviewId, "Subject " + reviewId, "Content " + reviewId);
		return client.post()
				.uri("/review")
				.body(Mono.just(review), Review.class)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isEqualTo(expectedStatus)
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBody();
	}
	
	private WebTestClient.BodyContentSpec getAndVerifyReviewsByProductId(int productId, HttpStatus expectedStatus) {
		return getAndVerifyReviewsByProductId("?productId=" + productId, expectedStatus);
	}
	
	private WebTestClient.BodyContentSpec getAndVerifyReviewsByProductId(String productIdQuery, HttpStatus expectedStatus) {
		return client.get()
				.uri("/review" + productIdQuery)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isEqualTo(expectedStatus)
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBody();
	}
	
	@Test
	void duplicateError() {
		int productId = 1;
		int reviewId = 1;

		assertEquals(0, repository.count());

		postAndVerifyReview(productId, reviewId, HttpStatus.OK)
				.jsonPath("$.productId").isEqualTo(productId)
				.jsonPath("$.reviewId").isEqualTo(reviewId);

		assertEquals(1, repository.count());

		postAndVerifyReview(productId, reviewId, HttpStatus.UNPROCESSABLE_CONTENT)
				.jsonPath("$.path").isEqualTo("/review")
				.jsonPath("$.message").isEqualTo("Duplicate key, Product Id: 1, Review Id:1");

		assertEquals(1, repository.count());
	}
	
	@Test
	void deleteReviews() {
		int productId = 1;
		int reviewId = 1;

		postAndVerifyReview(productId, reviewId, HttpStatus.OK);
		assertEquals(1, repository.findByProductId(productId).size());

		deleteAndVerifyReviewsByProductId(productId, HttpStatus.OK);
		assertEquals(0, repository.findByProductId(productId).size());

		deleteAndVerifyReviewsByProductId(productId, HttpStatus.OK);
	}
	
	private WebTestClient.BodyContentSpec deleteAndVerifyReviewsByProductId(int productId, HttpStatus expectedStatus) {
		return client.delete()
				.uri("/review?productId=" + productId)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isEqualTo(expectedStatus)
				.expectBody();
	}
	
	@Disabled("Not working")
	@Test
	void getReviewsMissingParameter() {
		getAndVerifyReviewsByProductId("", HttpStatus.BAD_REQUEST)
				.jsonPath("$.path").isEqualTo("/review")
				.jsonPath("$.message").isEqualTo("Required query parameter 'productId' is not present.");
	}
	
	@Disabled("Not working")
	@Test
	void getReviewsInvalidParameter() {
		getAndVerifyReviewsByProductId("?productId=no-integer", HttpStatus.BAD_REQUEST)
				.jsonPath("$.path").isEqualTo("/review")
				.jsonPath("$.message").isEqualTo("Type mismatch.");
	}
	
	@Test
	void getReviewsNotFound() {
		getAndVerifyReviewsByProductId("?productId=213", HttpStatus.OK)
				.jsonPath("$.length()").isEqualTo(0);
	}
	
	@Test
	void getReviewsInvalidParameterNegativeValue() {
		int productIdInvalid = -1;

		getAndVerifyReviewsByProductId("?productId=" + productIdInvalid, HttpStatus.UNPROCESSABLE_CONTENT)
				.jsonPath("$.path").isEqualTo("/review")
				.jsonPath("$.message").isEqualTo("Invalid productId: " + productIdInvalid);
	}

}