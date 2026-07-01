package se.magnus.composite.product.api.rest.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReviewSummaryRequestDto {
	
	private int productId;
	private String author;
	private String subject;
	private String content;

}
