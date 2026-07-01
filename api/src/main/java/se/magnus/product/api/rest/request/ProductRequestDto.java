package se.magnus.product.api.rest.request;

import lombok.Data;

@Data
public class ProductRequestDto {

	private final int productId;
	private final String name;
	private final int weight;

}
