package se.magnus.microservices.core.product.domain.service;

import se.magnus.microservices.core.product.domain.model.Product;
import se.magnus.microservices.core.product.persistence.ProductEntity;
import se.magnus.product.api.rest.request.ProductRequestDto;
import se.magnus.product.api.rest.response.ProductResponseDto;

public class ProductMapper {

	public static ProductEntity modelToEntity(Product product) {
		ProductEntity productEntity = new ProductEntity();
		productEntity.setName(product.getName());
		productEntity.setProductId(product.getProductId());
		productEntity.setWeight(product.getWeight());

		return productEntity;
	}

	public static Product dtoToModel(ProductRequestDto productRequestDto) {
		Product product = new Product();
		product.setProductId(productRequestDto.getProductId());
		product.setName(productRequestDto.getName());
		product.setWeight(productRequestDto.getWeight());

		return product;
	}

	public static ProductResponseDto modelToDto(Product product) {
		ProductResponseDto productResponseDto = new ProductResponseDto();
		productResponseDto.setProductId(product.getProductId());
		productResponseDto.setName(product.getName());
		productResponseDto.setWeight(product.getWeight());

		return productResponseDto;
	}

	public static Product entityToModel(ProductEntity entity) {
		Product product = new Product();
		product.setProductId(entity.getProductId());
		product.setName(entity.getName());
		product.setWeight(entity.getWeight());

		return product;

	}
}