package se.magnus.microservices.core.review.domain.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import se.magnus.api.rest.exceptions.InvalidInputException;
import se.magnus.microservices.core.review.domain.model.Review;
import se.magnus.microservices.core.review.domain.service.ReviewService;

@Service
@Slf4j
public class ReviewServiceImpl implements ReviewService {

	@Override
	public List<Review> getReviews(int productId) {
		if (productId < 1) {
			throw new InvalidInputException("Invalid productId: " + productId);
		}

		if (productId == 213) {
			log.debug("No reviews found for productId: {}", productId);
			return new ArrayList<>();
		}

		List<Review> list = new ArrayList<>();
		list.add(new Review(productId, 1, "Author 1", "Subject 1", "Content 1"));
		list.add(new Review(productId, 2, "Author 2", "Subject 2", "Content 2"));
		list.add(new Review(productId, 3, "Author 3", "Subject 3", "Content 3"));

		log.debug("/reviews response size: {}", list.size());

		return list;
	}

}
