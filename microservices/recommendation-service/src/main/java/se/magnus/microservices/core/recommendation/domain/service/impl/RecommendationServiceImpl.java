package se.magnus.microservices.core.recommendation.domain.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import se.magnus.api.rest.exceptions.InvalidInputException;
import se.magnus.microservices.core.recommendation.domain.model.Recommendation;
import se.magnus.microservices.core.recommendation.domain.service.RecommendationMapper;
import se.magnus.microservices.core.recommendation.domain.service.RecommendationService;
import se.magnus.microservices.core.recommendation.persistence.RecommendationEntity;
import se.magnus.microservices.core.recommendation.persistence.RecommendationRepository;

@Service
@Slf4j
public class RecommendationServiceImpl implements RecommendationService {

	private final RecommendationRepository repository;

	@Autowired
	public RecommendationServiceImpl(RecommendationRepository repository) {
		this.repository = repository;
	}

	@Override
	public List<Recommendation> getRecommendations(int productId) {

		if (productId < 1) {
			throw new InvalidInputException("Invalid productId: " + productId);
		}

		List<RecommendationEntity> entityList = repository.findByProductId(productId);
		List<Recommendation> list = RecommendationMapper.entityListToModelList(entityList);

		log.info("RecommendationServiceImpl.getRecommendations(): recommendation list size: {}", list.size());

		return list;
	}

	@Override
	public Recommendation createRecommendation(Recommendation recommendation) {
		try {
			RecommendationEntity entity = RecommendationMapper.modelToEntity(recommendation);
			RecommendationEntity newEntity = repository.save(entity);

			log.info("RecommendationServiceImpl.createRecommendation() :: creating recommendationId: {} for productId: {}", recommendation.getRecommendationId(), recommendation.getProductId());
			return RecommendationMapper.entityToModel(newEntity);

		} catch (DuplicateKeyException dke) {
			throw new InvalidInputException("Duplicate key, Product Id: " + recommendation.getProductId() + ", Recommendation Id:" + recommendation.getRecommendationId());
		}
	}

	@Override
	public void deleteRecommendations(int productId) {
		log.info("RecommendationServiceImpl.deleteRecommendations: tries to delete recommendations for the product with productId: {}", productId);
		repository.deleteAll(repository.findByProductId(productId));
	}

}
