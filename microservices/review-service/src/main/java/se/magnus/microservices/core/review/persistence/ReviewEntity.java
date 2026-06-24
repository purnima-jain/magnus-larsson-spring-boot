package se.magnus.microservices.core.review.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity // used to mark the class as an entity class used for JPA—mapped to a table in a SQL database with the name `reviews`.
@Table(name = "reviews", indexes = { @Index(name = "reviews_unique_idx", unique = true, columnList = "productId,reviewId") }) // used to specify that a unique compound index will be created for the
																																// compound business key based on the `productId` and `reviewId` fields.
@Data
@NoArgsConstructor
public class ReviewEntity {

	@Id
	@GeneratedValue // To direct Spring Data to JPA in order to automatically generate unique id values for the `id` field, we are using the `@GeneratedValue` annotation.
	private int id;

	@Version
	private int version;

	private int productId;
	private int reviewId;
	private String author;
	private String subject;
	private String content;

	public ReviewEntity(int productId, int reviewId, String author, String subject, String content) {
		this.productId = productId;
		this.reviewId = reviewId;
		this.author = author;
		this.subject = subject;
		this.content = content;
	}
}