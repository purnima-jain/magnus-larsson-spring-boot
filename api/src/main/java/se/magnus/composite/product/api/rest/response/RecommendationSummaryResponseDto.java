package se.magnus.composite.product.api.rest.response;

import lombok.Data;

@Data
public class RecommendationSummaryResponseDto {

	private final int recommendationId;
	private final String author;
	private final int rate;

}
