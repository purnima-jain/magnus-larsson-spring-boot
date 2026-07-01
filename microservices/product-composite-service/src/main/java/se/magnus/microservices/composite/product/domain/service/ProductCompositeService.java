package se.magnus.microservices.composite.product.domain.service;

import se.magnus.microservices.composite.product.domain.model.ProductAggregate;

public interface ProductCompositeService {

	ProductAggregate getProduct(int productId);

	ProductAggregate createCompositeProduct(ProductAggregate productAggregate);

	void deleteProduct(int productId);

}
