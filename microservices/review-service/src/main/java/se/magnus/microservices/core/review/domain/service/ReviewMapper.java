package se.magnus.microservices.core.review.domain.service;

import java.util.List;
import java.util.stream.Collectors;

import se.magnus.microservices.core.review.domain.model.Review;
import se.magnus.microservices.core.review.persistence.ReviewEntity;
import se.magnus.review.api.rest.response.ReviewRequestDto;
import se.magnus.review.api.rest.response.ReviewResponseDto;

public class ReviewMapper {

	public static Review dtoToModel(ReviewRequestDto reviewRequestDto) {
		Review review = new Review();
		review.setReviewId(reviewRequestDto.getReviewId());
		review.setProductId(reviewRequestDto.getProductId());
		review.setAuthor(reviewRequestDto.getAuthor());
		review.setContent(reviewRequestDto.getContent());
		review.setSubject(reviewRequestDto.getSubject());

		return review;
	}

	public static ReviewResponseDto modelToDto(Review review) {
		ReviewResponseDto reviewResponseDto = new ReviewResponseDto();
		reviewResponseDto.setReviewId(review.getReviewId());
		reviewResponseDto.setProductId(review.getProductId());
		reviewResponseDto.setAuthor(review.getAuthor());
		reviewResponseDto.setContent(review.getContent());
		reviewResponseDto.setSubject(review.getSubject());

		return reviewResponseDto;
	}

	public static List<Review> entityListToModelList(List<ReviewEntity> reviewEntityList) {
		List<Review> reviewList = reviewEntityList.stream()
				.map(reviewEntity -> new Review(reviewEntity.getProductId(), reviewEntity.getReviewId(), reviewEntity.getAuthor(), reviewEntity.getSubject(), reviewEntity.getContent()))
				.collect(Collectors.toList());
		return reviewList;
	}

	public static ReviewEntity modelToEntity(Review review) {
		ReviewEntity reviewEntity = new ReviewEntity();
		// reviewEntity.setId(review.getId());
		reviewEntity.setReviewId(review.getReviewId());
		reviewEntity.setProductId(review.getProductId());
		reviewEntity.setAuthor(review.getAuthor());
		reviewEntity.setContent(review.getContent());
		reviewEntity.setSubject(review.getSubject());
		// reviewEntity.setVersion(review.getVersion());

		return reviewEntity;
	}

	public static Review entityToModel(ReviewEntity reviewEntity) {
		Review review = new Review();
		review.setReviewId(reviewEntity.getReviewId());
		review.setProductId(reviewEntity.getProductId());
		review.setAuthor(reviewEntity.getAuthor());
		review.setContent(reviewEntity.getContent());
		review.setSubject(reviewEntity.getSubject());

		return review;
	}

}
