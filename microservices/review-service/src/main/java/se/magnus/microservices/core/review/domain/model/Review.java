package se.magnus.microservices.core.review.domain.model;

import lombok.Value;

@Value
public class Review {

	private final int productId;
	private final int reviewId;
	private final String author;
	private final String subject;
	private final String content;

}
