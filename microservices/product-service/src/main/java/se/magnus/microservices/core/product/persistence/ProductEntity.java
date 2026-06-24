package se.magnus.microservices.core.product.persistence;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "products") // used to mark the class as an entity class used for MongoDB, that is, mapped to a collection in MongoDB with the name `products`
@Data
@NoArgsConstructor
public class ProductEntity {

	@Id
	private String id;

	@Version
	private Integer version;

	@Indexed(unique = true) // used to get a unique index created for the business key, productId
	private int productId;

	private String name;
	private int weight;

	public ProductEntity(int productId, String name, int weight) {
		this.productId = productId;
		this.name = name;
		this.weight = weight;
	}
}