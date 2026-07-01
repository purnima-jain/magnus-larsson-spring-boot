package se.magnus.microservices.composite.product.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RecommendationSummary {

	private int recommendationId;
	private String author;
	private int rate;
	private String content;

	public RecommendationSummary(String author, int rate, String content) {
		this.author = author;
		this.rate = rate;
		this.content = content;
	}

}
