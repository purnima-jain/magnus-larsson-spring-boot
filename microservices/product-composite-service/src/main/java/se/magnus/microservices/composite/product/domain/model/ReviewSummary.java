package se.magnus.microservices.composite.product.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReviewSummary {

	private int reviewId;
	private String author;
	private String subject;
	private String content;

	public ReviewSummary(String author, String subject, String content) {
		this.author = author;
		this.subject = subject;
		this.content = content;
	}

}
