package se.magnus.product.api.rest.response;

import lombok.Value;

@Value
public class ProductResponseDto {

	private final int productId;
	private final String name;
	private final int weight;
	private final String serviceAddress;

}
