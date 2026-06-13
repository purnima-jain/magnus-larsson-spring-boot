package se.magnus.microservices.core.recommendation.rest.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import se.magnus.microservices.core.recommendation.domain.model.Recommendation;
import se.magnus.microservices.core.recommendation.domain.service.RecommendationService;
import se.magnus.recommendation.api.rest.response.RecommendationResponseDto;
import se.magnus.util.http.ServiceUtil;

@RestController
@Slf4j
public class RecommendationRestController {

	private final ServiceUtil serviceUtil;

	private final RecommendationService recommendationService;

	public RecommendationRestController(RecommendationService recommendationService, ServiceUtil serviceUtil) {
		this.recommendationService = recommendationService;
		this.serviceUtil = serviceUtil;
	}

	@GetMapping(value = "/recommendation", produces = "application/json")
	List<RecommendationResponseDto> getRecommendations(@RequestParam(value = "productId", required = true) int productId) {
		
		log.debug("/recommendation return the found recommendations for productId={}", productId);
		
		List<Recommendation> recommendationList = recommendationService.getRecommendations(productId);
		
		return recommendationList.stream()
			.map(recommendation -> new RecommendationResponseDto(
				recommendation.getProductId(),
				recommendation.getRecommendationId(),
				recommendation.getAuthor(),
				recommendation.getRate(),
				recommendation.getContent(),
				serviceUtil.getServiceAddress()
			))
			.toList();
	}

}
