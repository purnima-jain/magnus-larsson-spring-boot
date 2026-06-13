package se.magnus.microservices.core.recommendation.domain.model;

import lombok.Value;

@Value
public class Recommendation {

	private final int productId;
	private final int recommendationId;
	private final String author;
	private final int rate;
	private final String content;

}
