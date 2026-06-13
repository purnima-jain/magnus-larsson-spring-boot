package se.magnus.review.api.rest.response;

import lombok.Value;

@Value
public class ReviewResponseDto {

	private final int productId;
	private final int reviewId;
	private final String author;
	private final String subject;
	private final String content;
	private final String serviceAddress;

}
