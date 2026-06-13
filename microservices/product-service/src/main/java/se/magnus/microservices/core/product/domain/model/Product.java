package se.magnus.microservices.core.product.domain.model;

import lombok.Value;

@Value
public class Product {

	private final int productId;
	private final String name;
	private final int weight;
	
}
