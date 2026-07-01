package se.magnus.microservices.composite.product.rest.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import se.magnus.composite.product.api.rest.request.ProductAggregateRequestDto;
import se.magnus.composite.product.api.rest.response.ProductAggregateResponseDto;
import se.magnus.composite.product.api.rest.response.RecommendationSummaryResponseDto;
import se.magnus.composite.product.api.rest.response.ReviewSummaryResponseDto;
import se.magnus.composite.product.api.rest.response.ServiceAddressesResponseDto;
import se.magnus.microservices.composite.product.domain.model.ProductAggregate;
import se.magnus.microservices.composite.product.domain.model.RecommendationSummary;
import se.magnus.microservices.composite.product.domain.model.ReviewSummary;
import se.magnus.microservices.composite.product.domain.model.ServiceAddresses;
import se.magnus.microservices.composite.product.domain.service.ProductCompositeService;
import se.magnus.util.http.ServiceUtil;

@RestController
@Slf4j
@Tag(name = "ProductComposite", description = "REST API for composite product information.")
public class ProductCompositeRestController {

	private final ServiceUtil serviceUtil;
	private ProductCompositeService productCompositeService;

	public ProductCompositeRestController(ServiceUtil serviceUtil, ProductCompositeService productCompositeService) {
		this.serviceUtil = serviceUtil;
		this.productCompositeService = productCompositeService;
	}

	@Operation(summary = "${api.product-composite.create-composite-product.description}", description = "${api.product-composite.create-composite-product.notes}")
	@ApiResponses(value = { @ApiResponse(responseCode = "400", description = "${api.responseCodes.badRequest.description}"),
			@ApiResponse(responseCode = "422", description = "${api.responseCodes.unprocessableEntity.description}") })
	@PostMapping(value = "/product-composite", consumes = "application/json")
	ProductAggregateResponseDto createCompositeProduct(@RequestBody ProductAggregateRequestDto productAggregateRequestDto) {
		log.debug("ProductCompositeRestController.createCompositeProduct: creates a new composite entity for productId: {}", productAggregateRequestDto.getProductId());

		ProductAggregate productAggregate = new ProductAggregate();
		productAggregate.setProductId(productAggregateRequestDto.getProductId());
		productAggregate.setName(productAggregateRequestDto.getName());
		productAggregate.setWeight(productAggregateRequestDto.getWeight());

		if (productAggregateRequestDto.getRecommendations() != null) {
			productAggregateRequestDto.getRecommendations().forEach(r -> {
				RecommendationSummary recommendationSummary = new RecommendationSummary(r.getAuthor(), r.getRate(), r.getContent());
				productAggregate.getRecommendations().add(recommendationSummary);
			});
		}

		if (productAggregateRequestDto.getReviews() != null) {
			productAggregateRequestDto.getReviews().forEach(r -> {
				ReviewSummary reviewSummary = new ReviewSummary(r.getAuthor(), r.getSubject(), r.getContent());
				productAggregate.getReviews().add(reviewSummary);
			});
		}

		ProductAggregate newProductAggregate = productCompositeService.createCompositeProduct(productAggregate);

		ProductAggregateResponseDto productAggregateResponseDto = assembleProductAggregateResponseDto(newProductAggregate);

		log.debug("ProductCompositeRestController.createCompositeProduct: composite entities created for productId: {}", productAggregateResponseDto.getProductId());

		return productAggregateResponseDto;
	}

	private ProductAggregateResponseDto assembleProductAggregateResponseDto(ProductAggregate productAggregate) {
		List<RecommendationSummaryResponseDto> recommendationResponseList = productAggregate.getRecommendations().stream()
				.map(recommendation -> new RecommendationSummaryResponseDto(recommendation.getRecommendationId(), recommendation.getAuthor(), recommendation.getRate())).toList();

		List<ReviewSummaryResponseDto> reviewResponseList = productAggregate.getReviews().stream()
				.map(review -> new ReviewSummaryResponseDto(review.getReviewId(), review.getAuthor(), review.getSubject())).toList();

		ServiceAddresses serviceAddresses = productAggregate.getServiceAddresses();
		
		ServiceAddressesResponseDto serviceAddressesResponseDto = new ServiceAddressesResponseDto(serviceUtil.getServiceAddress(), serviceAddresses.getPro(), serviceAddresses.getRev(),
				serviceAddresses.getRec());

		ProductAggregateResponseDto productAggregateResponseDto = new ProductAggregateResponseDto(productAggregate.getProductId(), productAggregate.getName(), productAggregate.getWeight(),
				recommendationResponseList, reviewResponseList, serviceAddressesResponseDto);

		return productAggregateResponseDto;
	}

	@Operation(summary = "${api.product-composite.get-composite-product.description}", description = "${api.product-composite.get-composite-product.notes}")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "${api.responseCodes.ok.description}"),
			@ApiResponse(responseCode = "400", description = "${api.responseCodes.badRequest.description}"),
			@ApiResponse(responseCode = "404", description = "${api.responseCodes.notFound.description}"),
			@ApiResponse(responseCode = "422", description = "${api.responseCodes.unprocessableEntity.description}") })
	@GetMapping(value = "/product-composite/{productId}", produces = "application/json")
	public ProductAggregateResponseDto getCompositeProduct(@PathVariable int productId) {
		log.debug("/product-composite/{productId} return the found product for productId={}", productId);

		ProductAggregate productAggregate = productCompositeService.getProduct(productId);

		ProductAggregateResponseDto productAggregateResponseDto = assembleProductAggregateResponseDto(productAggregate);

		return productAggregateResponseDto;
	}

	@Operation(summary = "${api.product-composite.delete-composite-product.description}", description = "${api.product-composite.delete-composite-product.notes}")
	@ApiResponses(value = { @ApiResponse(responseCode = "400", description = "${api.responseCodes.badRequest.description}"),
			@ApiResponse(responseCode = "422", description = "${api.responseCodes.unprocessableEntity.description}") })
	@DeleteMapping(value = "/product-composite/{productId}")
	void deleteCompositeProduct(@PathVariable int productId) {
		log.debug("ProductCompositeRestController.deleteCompositeProduct: Deletes a product aggregate for productId: {}", productId);

		productCompositeService.deleteProduct(productId);

		log.debug("ProductCompositeRestController.deleteCompositeProduct: aggregate entities deleted for productId: {}", productId);
	}

}
