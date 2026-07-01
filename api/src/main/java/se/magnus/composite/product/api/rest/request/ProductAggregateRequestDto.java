package se.magnus.composite.product.api.rest.request;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductAggregateRequestDto {

	private int productId;
	private String name;
	private int weight;

	private List<RecommendationSummaryRequestDto> recommendations = new ArrayList<>();

	private List<ReviewSummaryRequestDto> reviews = new ArrayList<>();

}
