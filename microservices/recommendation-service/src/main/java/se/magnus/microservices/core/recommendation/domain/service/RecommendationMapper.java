package se.magnus.microservices.core.recommendation.domain.service;

import java.util.List;
import java.util.stream.Collectors;

import se.magnus.microservices.core.recommendation.domain.model.Recommendation;
import se.magnus.microservices.core.recommendation.persistence.RecommendationEntity;
import se.magnus.recommendation.api.rest.request.RecommendationRequestDto;
import se.magnus.recommendation.api.rest.response.RecommendationResponseDto;

public class RecommendationMapper {

	// --- entityListToModelList
	// --- modelToEntity
	// --- modelToDto

	// dtoToModel
	// --- modelToDto
	// -- entityToModel

	public static Recommendation entityToModel(RecommendationEntity entity) {
		Recommendation recommendation = new Recommendation();
		recommendation.setRecommendationId(entity.getRecommendationId());
		recommendation.setProductId(entity.getProductId());
		recommendation.setAuthor(entity.getAuthor());
		recommendation.setContent(entity.getContent());
		recommendation.setRate(entity.getRating());

		return recommendation;
	}

	public static RecommendationEntity modelToEntity(Recommendation recommendation) {
		RecommendationEntity recommendationEntity = new RecommendationEntity();
		// recommendationEntity.setId(id);
		recommendationEntity.setProductId(recommendation.getProductId());
		recommendationEntity.setAuthor(recommendation.getAuthor());
		recommendationEntity.setContent(recommendation.getContent());
		recommendationEntity.setRating(recommendation.getRate());
		recommendationEntity.setRecommendationId(recommendation.getRecommendationId());
		// recommendationEntity.setVersion(version);

		return recommendationEntity;
	}

	public static List<Recommendation> entityListToModelList(List<RecommendationEntity> recommendationEntityList) {
		List<Recommendation> recommendationList = recommendationEntityList.stream().map(recommendationEntity -> new Recommendation(recommendationEntity.getProductId(),
				recommendationEntity.getRecommendationId(), recommendationEntity.getAuthor(), recommendationEntity.getRating(), recommendationEntity.getContent())).collect(Collectors.toList());
		return recommendationList;
	}

	public static RecommendationResponseDto modelToDto(Recommendation recommendation) {
		RecommendationResponseDto recommendationResponseDto = new RecommendationResponseDto();
		recommendationResponseDto.setRecommendationId(recommendation.getRecommendationId());
		recommendationResponseDto.setProductId(recommendation.getProductId());
		recommendationResponseDto.setAuthor(recommendation.getAuthor());
		recommendationResponseDto.setContent(recommendation.getContent());
		recommendationResponseDto.setRate(recommendation.getRate());
		// recommendationResponseDto.setServiceAddress(null);

		return recommendationResponseDto;
	}

	public static Recommendation dtoToModel(RecommendationRequestDto recommendationRequestDto) {
		Recommendation recommendation = new Recommendation();
		recommendation.setRecommendationId(recommendationRequestDto.getRecommendationId());
		recommendation.setProductId(recommendationRequestDto.getProductId());
		recommendation.setAuthor(recommendationRequestDto.getAuthor());
		recommendation.setContent(recommendationRequestDto.getContent());
		recommendation.setRate(recommendationRequestDto.getRate());

		return recommendation;
	}
}