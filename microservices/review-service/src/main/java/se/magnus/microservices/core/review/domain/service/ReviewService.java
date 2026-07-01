package se.magnus.microservices.core.review.domain.service;

import java.util.List;

import se.magnus.microservices.core.review.domain.model.Review;

public interface ReviewService {

	List<Review> getReviews(int productId);

	Review createReview(Review review);

	void deleteReviews(int productId);

}
