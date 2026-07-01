package se.magnus.microservices.core.review.rest.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import se.magnus.microservices.core.review.domain.model.Review;
import se.magnus.microservices.core.review.domain.service.ReviewMapper;
import se.magnus.microservices.core.review.domain.service.ReviewService;
import se.magnus.review.api.rest.response.ReviewRequestDto;
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
	public List<ReviewResponseDto> getReviews(@RequestParam(value = "productId", required = true) int productId) {

		log.info("ReviewRestController.getReviews() :: fetching reviews for productId: {}", productId);

		List<Review> reviewList = reviewService.getReviews(productId);

		log.info("ReviewRestController.getReviews() :: found {} reviews", reviewList.size());

		return reviewList.stream()
				.map(review -> new ReviewResponseDto(review.getProductId(), review.getReviewId(), review.getAuthor(), review.getSubject(), review.getContent(), serviceUtil.getServiceAddress()))
				.toList();
	}

	@PostMapping(value = "/review", consumes = "application/json", produces = "application/json")
	public ReviewResponseDto createReview(@RequestBody ReviewRequestDto reviewRequestDto) {

		log.info("ReviewRestController.createReview() :: creating review: {}", reviewRequestDto);

		Review review = ReviewMapper.dtoToModel(reviewRequestDto);

		Review newReview = reviewService.createReview(review);
		log.info("ReviewRestController.createReview() :: entity created for productId: {}", reviewRequestDto.getProductId());
		
		ReviewResponseDto reviewResponseDto = ReviewMapper.modelToDto(newReview);
		reviewResponseDto.setServiceAddress(serviceUtil.getServiceAddress());

		return reviewResponseDto;

	}

	@DeleteMapping(value = "/review")
	public void deleteReviews(@RequestParam(value = "productId", required = true) int productId) {
		log.info("ReviewRestController.deleteReviews() :: tries to delete reviews for productId: {}", productId);
		reviewService.deleteReviews(productId);
	}

}
