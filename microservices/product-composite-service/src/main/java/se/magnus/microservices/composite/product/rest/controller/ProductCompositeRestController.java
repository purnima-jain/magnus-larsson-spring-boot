package se.magnus.microservices.composite.product.rest.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import se.magnus.composite.product.api.rest.response.ProductAggregateResponseDto;
import se.magnus.composite.product.api.rest.response.RecommendationSummaryResponseDto;
import se.magnus.composite.product.api.rest.response.ReviewSummaryResponseDto;
import se.magnus.composite.product.api.rest.response.ServiceAddressesResponseDto;
import se.magnus.microservices.composite.product.domain.model.ProductAggregate;
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

	
	/**
	   * Sample usage: "curl $HOST:$PORT/product-composite/1".
	   *
	   * @param productId Id of the product
	   * @return the composite product info, if found, else null
	   */
	  @Operation(
	    summary = "${api.product-composite.get-composite-product.description}",
	    description = "${api.product-composite.get-composite-product.notes}")
	  @ApiResponses(value = {
	    @ApiResponse(responseCode = "200", description = "${api.responseCodes.ok.description}"),
	    @ApiResponse(responseCode = "400", description = "${api.responseCodes.badRequest.description}"),
	    @ApiResponse(responseCode = "404", description = "${api.responseCodes.notFound.description}"),
	    @ApiResponse(responseCode = "422", description = "${api.responseCodes.unprocessableEntity.description}")
	  })
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

		ServiceAddresses serviceAddresses = productAggregate.getServiceAddresses();
		ServiceAddressesResponseDto serviceAddressesResponseDto = new ServiceAddressesResponseDto(serviceUtil.getServiceAddress(), serviceAddresses.getPro(), serviceAddresses.getRev(), serviceAddresses.getRec());
		
		ProductAggregateResponseDto productAggregateResponseDto = new ProductAggregateResponseDto(
				productAggregate.getProductId(),
				productAggregate.getName(),
				productAggregate.getWeight(),
				recommendationResponseList,
				reviewResponseList,
				serviceAddressesResponseDto);

		return productAggregateResponseDto;
	}

}
