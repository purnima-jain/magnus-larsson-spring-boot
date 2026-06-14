package se.magnus.microservices.composite.product.domain.model;

import lombok.Value;

@Value
public class RecommendationSummary {

	private final int recommendationId;
	private final String author;
	private final int rate;

}
