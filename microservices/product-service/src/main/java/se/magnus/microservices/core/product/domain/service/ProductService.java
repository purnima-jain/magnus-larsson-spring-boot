package se.magnus.microservices.core.product.domain.service;

import se.magnus.microservices.core.product.domain.model.Product;

public interface ProductService {

	Product getProduct(int productId);

}
