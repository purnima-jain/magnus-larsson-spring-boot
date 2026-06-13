package se.magnus.recommendation.api.rest.response;

import lombok.Value;

@Value
public class RecommendationResponseDto {

	private final int productId;
	private final int recommendationId;
	private final String author;
	private final int rate;
	private final String content;
	private final String serviceAddress;

}
