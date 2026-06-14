package se.magnus.microservices.composite.product.rest.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import se.magnus.composite.product.api.rest.response.ProductAggregateResponseDto;
import se.magnus.composite.product.api.rest.response.RecommendationSummaryResponseDto;
import se.magnus.composite.product.api.rest.response.ReviewSummaryResponseDto;
import se.magnus.microservices.composite.product.domain.model.ProductAggregate;
import se.magnus.microservices.composite.product.domain.service.ProductCompositeService;
import se.magnus.util.http.ServiceUtil;

@RestController
@Slf4j
public class ProductCompositeRestController {

	private final ServiceUtil serviceUtil;
	private ProductCompositeService productCompositeService;

	public ProductCompositeRestController(ServiceUtil serviceUtil, ProductCompositeService productCompositeService) {
		this.serviceUtil = serviceUtil;
		this.productCompositeService = productCompositeService;
	}

	@GetMapping(value = "/product-composite/{productId}",   produces = "application/json")
	public ProductAggregateResponseDto getProduct(@PathVariable int productId) {
		log.debug("/product-composite/{productId} return the found product for productId={}", productId);
		
		ProductAggregate productAggregate = productCompositeService.getProduct(productId);

		List<RecommendationSummaryResponseDto> recommendationResponseList = productAggregate.getRecommendations().stream()
			.map(recommendation -> new RecommendationSummaryResponseDto(
				recommendation.getRecommendationId(),
				recommendation.getAuthor(),
				recommendation.getRate()))
			.toList();

		List<ReviewSummaryResponseDto> reviewResponseList = productAggregate.getReviews().stream()
			.map(review -> new ReviewSummaryResponseDto(
				review.getReviewId(),
				review.getAuthor(),
				review.getSubject()))
			.toList();

		ProductAggregateResponseDto productAggregateResponseDto = new ProductAggregateResponseDto(
				productAggregate.getProductId(),
				productAggregate.getName(),
				productAggregate.getWeight(),
				recommendationResponseList,
				reviewResponseList,
				serviceUtil.getServiceAddress());

		return productAggregateResponseDto;
	}

}
