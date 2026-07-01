package se.magnus.composite.product.api.rest.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RecommendationSummaryRequestDto {

	private int productId;
	private String author;
	private int rate;
	private String content;

}
