package se.magnus.microservices.core.product.domain.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.dao.DuplicateKeyException;
import com.mongodb.MongoWriteException;

import lombok.extern.slf4j.Slf4j;
import se.magnus.api.rest.exceptions.InvalidInputException;
import se.magnus.api.rest.exceptions.NotFoundException;
import se.magnus.microservices.core.product.domain.model.Product;
import se.magnus.microservices.core.product.domain.service.ProductMapper;
import se.magnus.microservices.core.product.domain.service.ProductService;
import se.magnus.microservices.core.product.persistence.ProductEntity;
import se.magnus.microservices.core.product.persistence.ProductRepository;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

	private final ProductRepository repository;

	@Autowired
	public ProductServiceImpl(ProductRepository repository) {
		this.repository = repository;
	}

	@Override
	public Product getProduct(int productId) {
		
		ProductEntity entity = repository.findByProductId(productId)
			      .orElseThrow(() -> new NotFoundException("No product found for productId: " + productId));
		
		Product product = ProductMapper.entityToModel(entity);

		return product;
	}

	@Override
	public Product createProduct(Product product) {
		try {
			ProductEntity entity = ProductMapper.modelToEntity(product);
			ProductEntity newEntity = repository.save(entity);

			log.info("createProduct: entity created for productId: {}", product.getProductId());
			return ProductMapper.entityToModel(newEntity);

		} catch (DuplicateKeyException dke) {
			throw new InvalidInputException("Duplicate key, Product Id: " + product.getProductId());
		} catch (MongoWriteException mke) {
			throw new InvalidInputException("Duplicate key, Product Id: " + product.getProductId());
		}
	}

	@Override
	// The delete method uses the findByProductId() method in the repository and uses the ifPresent() method in the Optional class to conveniently delete the entity only if it exists. 
	// Note that the implementation is idempotent; it will not report any failure if the entity is not found.
	public void deleteProduct(int productId) {
		log.info("deleteProduct: tries to delete an entity with productId: {}", productId);
		repository.findByProductId(productId).ifPresent(e -> repository.delete(e));
	}

}
