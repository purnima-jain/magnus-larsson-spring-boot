package se.magnus.review.api.rest.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReviewRequestDto {

	private int productId;
	private int reviewId;
	private String author;
	private String subject;
	private String content;

}
