package se.magnus.microservices.core.product.domain.service.impl;

import org.springframework.stereotype.Service;

import se.magnus.api.rest.exceptions.InvalidInputException;
import se.magnus.api.rest.exceptions.NotFoundException;
import se.magnus.microservices.core.product.domain.model.Product;
import se.magnus.microservices.core.product.domain.service.ProductService;

@Service
public class ProductServiceImpl implements ProductService {

	@Override
	public Product getProduct(int productId) {
		if (productId < 1) {
			throw new InvalidInputException("Invalid productId: " + productId);
		}

		if (productId == 13) {
			throw new NotFoundException("No product found for productId: " + productId);
		}

		return new Product(productId, "name-" + productId, 123);
	}

}
