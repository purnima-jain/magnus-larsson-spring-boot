package se.magnus.recommendation.api.rest.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationRequestDto {

	private int productId;
	private int recommendationId;
	private String author;
	private int rate;
	private String content;

}
