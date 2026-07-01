package se.magnus.microservices.core.recommendation.domain.service;

import java.util.List;

import se.magnus.microservices.core.recommendation.domain.model.Recommendation;

public interface RecommendationService {

	public List<Recommendation> getRecommendations(int productId);

	public Recommendation createRecommendation(Recommendation recommendation);

	public void deleteRecommendations(int productId);

}
