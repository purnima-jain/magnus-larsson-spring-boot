package se.magnus.microservices.core.product.persistence;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ProductRepository extends CrudRepository<ProductEntity, String>, PagingAndSortingRepository<ProductEntity, String> {

	// Since it might return zero or one product entity, the return value is marked as Optional
	Optional<ProductEntity> findByProductId(int productId);

}
