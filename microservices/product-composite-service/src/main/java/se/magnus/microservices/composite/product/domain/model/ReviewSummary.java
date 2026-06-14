package se.magnus.microservices.composite.product.domain.model;

import lombok.Value;

@Value
public class ReviewSummary {

	private final int reviewId;
	private final String author;
	private final String subject;

}
