package se.magnus.microservices.composite.product.domain.model;

import java.util.List;

import lombok.Value;

@Value
public class ProductAggregate {

	private final int productId;
	private final String name;
	private final int weight;
	private final List<RecommendationSummary> recommendations;
	private final List<ReviewSummary> reviews;

}
