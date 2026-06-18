package se.magnus.composite.product.api.rest.response;

import java.util.List;

import lombok.Value;

@Value
public class ProductAggregateResponseDto {

	private final int productId;
	private final String name;
	private final int weight;
	private final List<RecommendationSummaryResponseDto> recommendations;
	private final List<ReviewSummaryResponseDto> reviews;
	
	private final ServiceAddressesResponseDto serviceAddresses;

}
