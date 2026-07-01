package se.magnus.microservices.core.review.domain.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import se.magnus.api.rest.exceptions.InvalidInputException;
import se.magnus.microservices.core.review.domain.model.Review;
import se.magnus.microservices.core.review.domain.service.ReviewMapper;
import se.magnus.microservices.core.review.domain.service.ReviewService;
import se.magnus.microservices.core.review.persistence.ReviewEntity;
import se.magnus.microservices.core.review.persistence.ReviewRepository;

@Service
@Slf4j
public class ReviewServiceImpl implements ReviewService {

	private final ReviewRepository repository;

	@Autowired
	public ReviewServiceImpl(ReviewRepository repository) {
		this.repository = repository;
	}

	@Override
	public List<Review> getReviews(int productId) {
		if (productId < 1) {
			throw new InvalidInputException("Invalid productId: " + productId);
		}

		List<ReviewEntity> entityList = repository.findByProductId(productId);
		List<Review> list = ReviewMapper.entityListToModelList(entityList);

		log.info("ReviewServiceImpl.getReviews(): review list size: {}", list.size());
		log.debug("/review response size: {}", list.size());

		return list;
	}

	@Override
	public Review createReview(Review review) {
		try {
			ReviewEntity entity = ReviewMapper.modelToEntity(review);
			ReviewEntity newEntity = repository.save(entity);

			log.info("ReviewServiceImpl.createReview() :: creating reviewId: {} for productId: {}", review.getReviewId(), review.getProductId());
			return ReviewMapper.entityToModel(newEntity);

		} catch (DataIntegrityViolationException dive) {
			throw new InvalidInputException("Duplicate key, Product Id: " + review.getProductId() + ", Review Id:" + review.getReviewId());
		}
	}

	@Override
	public void deleteReviews(int productId) {
		log.info("ReviewServiceImpl.deleteReviews: tries to delete reviews for the product with productId: {}", productId);
		repository.deleteAll(repository.findByProductId(productId));
	}

}
