package se.magnus.product.api.rest.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponseDto {

	private int productId;
	private String name;
	private int weight;
	private String serviceAddress;

}
