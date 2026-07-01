package se.magnus.recommendation.api.rest.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecommendationResponseDto {

	private int productId;
	private int recommendationId;
	private String author;
	private int rate;
	private String content;
	private String serviceAddress;

}
