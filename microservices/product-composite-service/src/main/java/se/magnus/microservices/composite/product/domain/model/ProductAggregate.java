package se.magnus.microservices.composite.product.domain.model;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductAggregate {

	private int productId;
	private String name;
	private int weight;
	private List<RecommendationSummary> recommendations = new ArrayList<>();
	private List<ReviewSummary> reviews = new ArrayList<>();

	private ServiceAddresses serviceAddresses;

}
