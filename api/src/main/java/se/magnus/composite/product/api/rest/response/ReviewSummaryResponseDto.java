package se.magnus.composite.product.api.rest.response;

import lombok.Value;

@Value
public class ReviewSummaryResponseDto {
	private final int reviewId;
	private final String author;
	private final String subject;
}
