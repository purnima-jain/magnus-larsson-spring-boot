package se.magnus.microservices.composite.product.domain.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import se.magnus.api.rest.exceptions.NotFoundException;
import se.magnus.microservices.composite.product.domain.model.ProductAggregate;
import se.magnus.microservices.composite.product.domain.model.RecommendationSummary;
import se.magnus.microservices.composite.product.domain.model.ReviewSummary;
import se.magnus.microservices.composite.product.domain.model.ServiceAddresses;
import se.magnus.microservices.composite.product.domain.service.ProductCompositeService;
import se.magnus.product.api.rest.request.ProductRequestDto;
import se.magnus.product.api.rest.response.ProductResponseDto;
import se.magnus.recommendation.api.rest.request.RecommendationRequestDto;
import se.magnus.recommendation.api.rest.response.RecommendationResponseDto;
import se.magnus.review.api.rest.response.ReviewRequestDto;
import se.magnus.review.api.rest.response.ReviewResponseDto;

@Service
@Slf4j
public class ProductCompositeServiceImpl implements ProductCompositeService {

	private ProductCompositeIntegrationService productCompositeIntegrationService;

	public ProductCompositeServiceImpl(ProductCompositeIntegrationService productCompositeIntegrationService) {
		this.productCompositeIntegrationService = productCompositeIntegrationService;
	}

	@Override
	public ProductAggregate getProduct(int productId) {
		ProductResponseDto productResponseDto = productCompositeIntegrationService.getProduct(productId);
		if (productResponseDto == null) {
			throw new NotFoundException("No product found for productId: " + productId);
		}

		List<RecommendationResponseDto> recommendationResponseDtoList = productCompositeIntegrationService.getRecommendations(productId);

		List<ReviewResponseDto> reviewResponseDtoList = productCompositeIntegrationService.getReviews(productId);

		// 4. Create info regarding the involved microservices addresses
		String productAddress = productResponseDto.getServiceAddress();
		String reviewAddress = (reviewResponseDtoList != null && reviewResponseDtoList.size() > 0) ? reviewResponseDtoList.get(0).getServiceAddress() : "";
		String recommendationAddress = (recommendationResponseDtoList != null && recommendationResponseDtoList.size() > 0) ? recommendationResponseDtoList.get(0).getServiceAddress() : "";
		ServiceAddresses serviceAddresses = new ServiceAddresses(null, productAddress, reviewAddress, recommendationAddress);

		return assembleProductAggregate(productResponseDto, recommendationResponseDtoList, reviewResponseDtoList, serviceAddresses);

	}

	private ProductAggregate assembleProductAggregate(ProductResponseDto productResponseDto, List<RecommendationResponseDto> recommendationResponseDtoList,
			List<ReviewResponseDto> reviewResponseDtoList, ServiceAddresses serviceAddresses) {

		// 1. Setup product info
		int productId = productResponseDto.getProductId();
		String name = productResponseDto.getName();
		int weight = productResponseDto.getWeight();

		// 2. Copy summary recommendation info, if available
		List<RecommendationSummary> recommendationSummaries = (recommendationResponseDtoList == null) ? null
				: recommendationResponseDtoList.stream().map(r -> new RecommendationSummary(r.getRecommendationId(), r.getAuthor(), r.getRate(), r.getContent())).collect(Collectors.toList());

		// 3. Copy summary review info, if available
		List<ReviewSummary> reviewSummaries = (reviewResponseDtoList == null) ? null
				: reviewResponseDtoList.stream().map(r -> new ReviewSummary(r.getReviewId(), r.getAuthor(), r.getSubject(), r.getContent())).collect(Collectors.toList());

		return new ProductAggregate(productId, name, weight, recommendationSummaries, reviewSummaries, serviceAddresses);
	}

	@Override
	public ProductAggregate createCompositeProduct(ProductAggregate productAggregate) {
		ProductAggregate newProductAggregate = new ProductAggregate();
		
		try {
			ProductRequestDto productRequestDto = new ProductRequestDto(productAggregate.getProductId(), productAggregate.getName(), productAggregate.getWeight());
			ProductResponseDto productResponseDto = productCompositeIntegrationService.createProduct(productRequestDto);
			
			newProductAggregate.setProductId(productResponseDto.getProductId());
			newProductAggregate.setName(productResponseDto.getName());
			newProductAggregate.setWeight(productResponseDto.getWeight());
			
			newProductAggregate.setServiceAddresses(new ServiceAddresses());
			newProductAggregate.getServiceAddresses().setPro(productResponseDto.getServiceAddress());
			
			if (productAggregate.getRecommendations() != null) {
				log.info("Size: {}", productAggregate.getRecommendations().size());
				productAggregate.getRecommendations().forEach(r -> {
					RecommendationRequestDto recommendationRequestDto = new RecommendationRequestDto(productAggregate.getProductId(), r.getRecommendationId(), r.getAuthor(), r.getRate(), r.getContent());
					RecommendationResponseDto recommendationResponseDto = productCompositeIntegrationService.createRecommendation(recommendationRequestDto);
					RecommendationSummary recommendationSummary = new RecommendationSummary(recommendationResponseDto.getRecommendationId(), recommendationResponseDto.getAuthor(), recommendationResponseDto.getRate(), recommendationResponseDto.getContent());
					newProductAggregate.getServiceAddresses().setRec(recommendationResponseDto.getServiceAddress());
					newProductAggregate.getRecommendations().add(recommendationSummary);					
				});
			}

			if (productAggregate.getReviews() != null) {
				productAggregate.getReviews().forEach(r -> {
					ReviewRequestDto reviewRequestDto = new ReviewRequestDto(productAggregate.getProductId(), r.getReviewId(), r.getAuthor(), r.getSubject(), r.getContent());
					ReviewResponseDto reviewResponseDto = productCompositeIntegrationService.createReview(reviewRequestDto);
					ReviewSummary reviewSummary = new ReviewSummary(reviewResponseDto.getReviewId(), reviewResponseDto.getAuthor(), reviewResponseDto.getSubject(), reviewResponseDto.getContent());
					newProductAggregate.getServiceAddresses().setRev(reviewResponseDto.getServiceAddress());
					newProductAggregate.getReviews().add(reviewSummary);					
				});
			}

		} catch (RuntimeException re) {
			log.warn("createCompositeProduct failed", re);
			throw re;
		}
		
		return newProductAggregate;
	}

	@Override
	public void deleteProduct(int productId) {
		productCompositeIntegrationService.deleteProduct(productId);

		productCompositeIntegrationService.deleteRecommendations(productId);

		productCompositeIntegrationService.deleteReviews(productId);		
	}

}
