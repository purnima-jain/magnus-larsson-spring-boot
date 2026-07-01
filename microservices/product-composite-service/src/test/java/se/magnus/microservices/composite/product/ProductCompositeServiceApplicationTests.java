package se.magnus.microservices.composite.product;

import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.core.publisher.Mono;
import se.magnus.api.rest.exceptions.InvalidInputException;
import se.magnus.api.rest.exceptions.NotFoundException;
import se.magnus.composite.product.api.rest.request.ProductAggregateRequestDto;
import se.magnus.composite.product.api.rest.request.RecommendationSummaryRequestDto;
import se.magnus.composite.product.api.rest.request.ReviewSummaryRequestDto;
import se.magnus.microservices.composite.product.domain.service.impl.ProductCompositeIntegrationService;
import se.magnus.product.api.rest.request.ProductRequestDto;
import se.magnus.product.api.rest.response.ProductResponseDto;
import se.magnus.recommendation.api.rest.response.RecommendationResponseDto;
import se.magnus.review.api.rest.response.ReviewRequestDto;
import se.magnus.review.api.rest.response.ReviewResponseDto;
import se.magnus.recommendation.api.rest.request.RecommendationRequestDto;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class ProductCompositeServiceApplicationTests {

	private static final int PRODUCT_ID_OK = 1;
	private static final int PRODUCT_ID_NOT_FOUND = 2;
	private static final int PRODUCT_ID_INVALID = 3;

	@Autowired
	private WebTestClient client;

	@MockitoBean
	private ProductCompositeIntegrationService productCompositeIntegrationService;

	@BeforeEach
	void setUp() {

		when(productCompositeIntegrationService.getProduct(PRODUCT_ID_OK)).thenReturn(new ProductResponseDto(PRODUCT_ID_OK, "name", 1, "mock-address"));

		when(productCompositeIntegrationService.getRecommendations(PRODUCT_ID_OK))
				.thenReturn(Collections.singletonList(new RecommendationResponseDto(PRODUCT_ID_OK, 1, "author", 1, "content", "mock address")));
		
		when(productCompositeIntegrationService.createProduct(ArgumentMatchers.any(ProductRequestDto.class))).thenReturn(new ProductResponseDto(1, "name_a", 1, "service_address_a"));
		
		when(productCompositeIntegrationService.createRecommendation(ArgumentMatchers.any(RecommendationRequestDto.class))).thenReturn(new RecommendationResponseDto(1, 1, "author_a", 1, "content_a", "service_address_a"));
		
		when(productCompositeIntegrationService.createReview(ArgumentMatchers.any(ReviewRequestDto.class))).thenReturn(new ReviewResponseDto(1, 1, "author_a", "subject_a", "content_a", "service_address_a"));

		when(productCompositeIntegrationService.getReviews(PRODUCT_ID_OK))
				.thenReturn(Collections.singletonList(new ReviewResponseDto(PRODUCT_ID_OK, 1, "author", "subject", "content", "mock address")));

		when(productCompositeIntegrationService.getProduct(PRODUCT_ID_NOT_FOUND)).thenThrow(new NotFoundException("NOT FOUND: " + PRODUCT_ID_NOT_FOUND));

		when(productCompositeIntegrationService.getProduct(PRODUCT_ID_INVALID)).thenThrow(new InvalidInputException("INVALID: " + PRODUCT_ID_INVALID));
		
		
	}

	@Test
	void contextLoads() {
	}

	@Test
	void createCompositeProduct1() {
		ProductAggregateRequestDto productAggregateRequestDto = new ProductAggregateRequestDto(1, "name", 1, null, null);

		postAndVerifyProduct(productAggregateRequestDto, HttpStatus.OK);
	}

	private void postAndVerifyProduct(ProductAggregateRequestDto productAggregateRequestDto, HttpStatus expectedStatus) {
		client.post()
			.uri("/product-composite")
			.body(Mono.just(productAggregateRequestDto), ProductAggregateRequestDto.class)
			.exchange()
			.expectStatus()
			.isEqualTo(expectedStatus);
	}
	
	@Test
	void createCompositeProduct2() {
		ProductAggregateRequestDto productAggregateRequestDto = new ProductAggregateRequestDto(1, "name", 1, 
												Collections.singletonList(new RecommendationSummaryRequestDto(1, "a", 1, "c")), 
												Collections.singletonList(new ReviewSummaryRequestDto(1, "a", "s", "c")));

		postAndVerifyProduct(productAggregateRequestDto, HttpStatus.OK);
	}
	
	@Test
	void deleteCompositeProduct() {
		ProductAggregateRequestDto productAggregateRequestDto = new ProductAggregateRequestDto(1, "name", 1, 
												Collections.singletonList(new RecommendationSummaryRequestDto(1, "a", 1, "c")), 
												Collections.singletonList(new ReviewSummaryRequestDto(1, "a", "s", "c")));

		postAndVerifyProduct(productAggregateRequestDto, HttpStatus.OK);

		deleteAndVerifyProduct(productAggregateRequestDto.getProductId(), HttpStatus.OK);
		deleteAndVerifyProduct(productAggregateRequestDto.getProductId(), HttpStatus.OK);
	}
	
	private void deleteAndVerifyProduct(int productId, HttpStatus expectedStatus) {
		client.delete()
			.uri("/product-composite/" + productId)
			.exchange()
			.expectStatus()
			.isEqualTo(expectedStatus);
	}
	
	@Test
	void getProductById() {
		getAndVerifyProduct(PRODUCT_ID_OK, HttpStatus.OK)
			.jsonPath("$.productId").isEqualTo(PRODUCT_ID_OK)
			.jsonPath("$.recommendations.length()").isEqualTo(1)
			.jsonPath("$.reviews.length()").isEqualTo(1);
	}
	
	private WebTestClient.BodyContentSpec getAndVerifyProduct(int productId, HttpStatus expectedStatus) {
		return client.get()
					.uri("/product-composite/" + productId)
					.accept(MediaType.APPLICATION_JSON)
					.exchange()
					.expectStatus().isEqualTo(expectedStatus)
					.expectHeader().contentType(MediaType.APPLICATION_JSON)
					.expectBody();
	}
	
	@Test
	void getProductNotFound() {
		getAndVerifyProduct(PRODUCT_ID_NOT_FOUND, HttpStatus.NOT_FOUND)
			.jsonPath("$.path").isEqualTo("/product-composite/" + PRODUCT_ID_NOT_FOUND)
			.jsonPath("$.message").isEqualTo("NOT FOUND: " + PRODUCT_ID_NOT_FOUND);
	}
	
	@Test
	void getProductInvalidInput() {
		getAndVerifyProduct(PRODUCT_ID_INVALID, HttpStatus.UNPROCESSABLE_CONTENT)
			.jsonPath("$.path").isEqualTo("/product-composite/" + PRODUCT_ID_INVALID)
			.jsonPath("$.message").isEqualTo("INVALID: " + PRODUCT_ID_INVALID);
	}	

}
