package se.magnus.microservices.core.recommendation.rest.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import se.magnus.microservices.core.recommendation.domain.model.Recommendation;
import se.magnus.microservices.core.recommendation.domain.service.RecommendationMapper;
import se.magnus.microservices.core.recommendation.domain.service.RecommendationService;
import se.magnus.recommendation.api.rest.request.RecommendationRequestDto;
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
		log.info("RecommendationRestController.getRecommendations() :: fetching recommendations for productId: {}", productId);

		List<Recommendation> recommendationList = recommendationService.getRecommendations(productId);

		log.info("RecommendationRestController.getRecommendations() :: found {} recommendations", recommendationList.size());

		return recommendationList.stream().map(recommendation -> new RecommendationResponseDto(recommendation.getProductId(), recommendation.getRecommendationId(), recommendation.getAuthor(),
				recommendation.getRate(), recommendation.getContent(), serviceUtil.getServiceAddress())).toList();
	}

	@PostMapping(value = "/recommendation", consumes = "application/json", produces = "application/json")
	RecommendationResponseDto createRecommendation(@RequestBody RecommendationRequestDto recommendationRequestDto) {
		log.info("RecommendationRestController.createRecommendation() :: creating recommendation: {}", recommendationRequestDto);

		Recommendation recommendation = RecommendationMapper.dtoToModel(recommendationRequestDto);
		Recommendation newRecommendation = recommendationService.createRecommendation(recommendation);
		log.info("RecommendationRestController.createRecommendation() :: entity created for recommendationId: {}", newRecommendation.getRecommendationId());
		
		RecommendationResponseDto recommendationResponseDto = RecommendationMapper.modelToDto(newRecommendation);
		recommendationResponseDto.setServiceAddress(serviceUtil.getServiceAddress());

		return recommendationResponseDto;
	}

	@DeleteMapping(value = "/recommendation")
	void deleteRecommendations(@RequestParam(value = "productId", required = true) int productId) {
		log.info("RecommendationRestController.deleteRecommendations() :: tries to delete recommendations for productId: {}", productId);
		recommendationService.deleteRecommendations(productId);
	}

}
