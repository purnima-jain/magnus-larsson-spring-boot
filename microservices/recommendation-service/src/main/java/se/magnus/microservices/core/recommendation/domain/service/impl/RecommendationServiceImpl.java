package se.magnus.microservices.core.recommendation.domain.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import se.magnus.api.rest.exceptions.InvalidInputException;
import se.magnus.microservices.core.recommendation.domain.model.Recommendation;
import se.magnus.microservices.core.recommendation.domain.service.RecommendationService;

@Service
@Slf4j
public class RecommendationServiceImpl implements RecommendationService {

	@Override
	public List<Recommendation> getRecommendations(int productId) {

		if (productId < 1) {
			throw new InvalidInputException("Invalid productId: " + productId);
		}

		if (productId == 113) {
			log.debug("No recommendations found for productId: {}", productId);
			return new ArrayList<>();
		}

		List<Recommendation> list = new ArrayList<>();
		list.add(new Recommendation(productId, 1, "Author 1", 1, "Content 1"));
		list.add(new Recommendation(productId, 2, "Author 2", 2, "Content 2"));
		list.add(new Recommendation(productId, 3, "Author 3", 3, "Content 3"));

		log.debug("/recommendation response size: {}", list.size());

		return list;
	}

}
