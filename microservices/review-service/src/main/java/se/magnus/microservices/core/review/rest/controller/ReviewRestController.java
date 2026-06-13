package se.magnus.microservices.core.review.rest.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import se.magnus.microservices.core.review.domain.model.Review;
import se.magnus.microservices.core.review.domain.service.ReviewService;
import se.magnus.review.api.rest.response.ReviewResponseDto;
import se.magnus.util.http.ServiceUtil;

@RestController
@Slf4j
public class ReviewRestController {

	private final ServiceUtil serviceUtil;

	private final ReviewService reviewService;

	public ReviewRestController(ReviewService reviewService, ServiceUtil serviceUtil) {
		this.reviewService = reviewService;
		this.serviceUtil = serviceUtil;
	}

	@GetMapping(value = "/review", produces = "application/json")
	List<ReviewResponseDto> getReviews(@RequestParam(value = "productId", required = true) int productId) {
		
	log.debug("/review return the found reviews for productId={}", productId);
		
		List<Review> reviewList = reviewService.getReviews(productId);
		
		return reviewList.stream()
			.map(review -> new ReviewResponseDto(
				review.getProductId(),
				review.getReviewId(),
				review.getAuthor(),
				review.getSubject(),
				review.getContent(),
				serviceUtil.getServiceAddress()))
			.toList();
	}

}
