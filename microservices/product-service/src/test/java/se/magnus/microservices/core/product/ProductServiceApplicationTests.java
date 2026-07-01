package se.magnus.microservices.core.product;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import se.magnus.microservices.core.product.persistence.ProductRepository;
import se.magnus.product.api.rest.request.ProductRequestDto;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class ProductServiceApplicationTests extends MongoDbTestBase {

	@Autowired
	private WebTestClient client;

	@Autowired
	private ProductRepository repository;

	@BeforeEach
	void setupDb() {
		repository.deleteAll();
	}
	
	@Test
	void getProductById() {
		int productId = 1;

		postAndVerifyProduct(productId, HttpStatus.OK);

		assertTrue(repository.findByProductId(productId).isPresent());

		getAndVerifyProduct(productId, HttpStatus.OK).jsonPath("$.productId").isEqualTo(productId);
	}
	
	private WebTestClient.BodyContentSpec getAndVerifyProduct(int productId, HttpStatus expectedStatus) {
		return getAndVerifyProduct("/" + productId, expectedStatus);
	}
	
	private WebTestClient.BodyContentSpec getAndVerifyProduct(String productIdPath, HttpStatus expectedStatus) {
		return client.get()
				.uri("/product" + productIdPath)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isEqualTo(expectedStatus)
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBody()
				.consumeWith(result -> System.out.println(result));
	}

	@Test
	@Disabled("Not working")
	void duplicateError() {
		int productId = 1;

		postAndVerifyProduct(productId, HttpStatus.OK);

		assertTrue(repository.findByProductId(productId).isPresent());

		postAndVerifyProduct(productId, HttpStatus.UNPROCESSABLE_CONTENT)
						.jsonPath("$.path").isEqualTo("/product")
						.jsonPath("$.message").isEqualTo("Duplicate key, Product Id: " + productId);
	}

//	private WebTestClient.BodyContentSpec postAndVerifyProduct(int productId, HttpStatus expectedStatus) {
//		Product product = new Product(productId, "Name " + productId, productId);
//		return client.post()
//			      .uri("/product")
//			      .body(Mono.just(product), Product.class)
//			      .accept(MediaType.APPLICATION_JSON)
//			      .exchange()
//			      .expectStatus().isEqualTo(expectedStatus)
//			      .expectHeader().contentType(MediaType.APPLICATION_JSON)
//			      .expectBody();
//	}
	
	private WebTestClient.BodyContentSpec postAndVerifyProduct(int productId, HttpStatus expectedStatus) {
		ProductRequestDto productRequestDto = new ProductRequestDto(productId, "Name " + productId, productId);
		return client.post()
			      .uri("/product")
			      .body(Mono.just(productRequestDto), ProductRequestDto.class)
			      .accept(MediaType.APPLICATION_JSON)
			      .exchange()
			      .expectStatus().isEqualTo(expectedStatus)
			      .expectHeader().contentType(MediaType.APPLICATION_JSON)
			      .expectBody();
	}

	@Test
	void deleteProduct() {
		int productId = 1;

		postAndVerifyProduct(productId, HttpStatus.OK);
		assertTrue(repository.findByProductId(productId).isPresent());

		deleteAndVerifyProduct(productId, HttpStatus.OK);
		assertFalse(repository.findByProductId(productId).isPresent());

		deleteAndVerifyProduct(productId, HttpStatus.OK);
	}
	
	private WebTestClient.BodyContentSpec deleteAndVerifyProduct(int productId, HttpStatus expectedStatus) {
		return client.delete()
				.uri("/product/" + productId)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isEqualTo(expectedStatus)
				.expectBody();
	}
	
	@Disabled("Not working")
	@Test
	void getProductInvalidParameterString() {
		getAndVerifyProduct("/no-integer", HttpStatus.BAD_REQUEST)
				.jsonPath("$.path").isEqualTo("/product/no-integer")
				.jsonPath("$.message").isEqualTo("Type mismatch.");
	}
	
	@Test
	void getProductNotFound() {
		int productIdNotFound = 13;
		
		getAndVerifyProduct(productIdNotFound, HttpStatus.NOT_FOUND)
				.jsonPath("$.path").isEqualTo("/product/" + productIdNotFound)
				.jsonPath("$.message").isEqualTo("No product found for productId: " + productIdNotFound);
	}
	
	@Test
	void getProductInvalidParameterNegativeValue() {

		int productIdInvalid = -1;

		getAndVerifyProduct(productIdInvalid, HttpStatus.UNPROCESSABLE_CONTENT)
				.jsonPath("$.path").isEqualTo("/product/" + productIdInvalid)
				.jsonPath("$.message").isEqualTo("Invalid productId: " + productIdInvalid);
	}

}
