package se.magnus.microservices.core.product.rest.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import se.magnus.api.rest.exceptions.InvalidInputException;
import se.magnus.microservices.core.product.domain.model.Product;
import se.magnus.microservices.core.product.domain.service.ProductMapper;
import se.magnus.microservices.core.product.domain.service.ProductService;
import se.magnus.product.api.rest.request.ProductRequestDto;
import se.magnus.product.api.rest.response.ProductResponseDto;
import se.magnus.util.http.ServiceUtil;

@RestController
@Slf4j
public class ProductRestController {

	private final ProductService productService;

	private final ServiceUtil serviceUtil;

	public ProductRestController(ProductService productService, ServiceUtil serviceUtil) {
		this.productService = productService;
		this.serviceUtil = serviceUtil;
	}

	@GetMapping(value = "/product/{productId}", produces = "application/json")
	public ProductResponseDto getProduct(@PathVariable int productId) {		
		log.info("ProductRestController.getProduct() :: fetching product details for productId: {}", productId);

		if (productId < 1) {
			throw new InvalidInputException("Invalid productId: " + productId);
		}

		Product product = productService.getProduct(productId);

		ProductResponseDto productResponseDto = ProductMapper.modelToDto(product);
		productResponseDto.setServiceAddress(serviceUtil.getServiceAddress());

		log.info("ProductRestController.getProduct() :: found productId: {}", productResponseDto.getProductId());

		return productResponseDto;
	}

	@PostMapping(value = "/product", consumes = "application/json", produces = "application/json")
	public ProductResponseDto createProduct(@RequestBody ProductRequestDto productRequestDto) {
		log.info("ProductRestController.createProduct() :: creating product: {}", productRequestDto);

		Product product = ProductMapper.dtoToModel(productRequestDto);

		Product newProduct = productService.createProduct(product);
		log.info("ProductRestController.createProduct() :: entity created for productId: {}", productRequestDto.getProductId());
		
		ProductResponseDto productResponseDto = ProductMapper.modelToDto(newProduct);
		productResponseDto.setServiceAddress(serviceUtil.getServiceAddress());

		return productResponseDto;
	}

	// The implementation of the delete operation will be idempotent; this implies that the operation should return the status code OK (200) even though the entity no longer exists in the database.
	@DeleteMapping(value = "/product/{productId}")
	public void deleteProduct(@PathVariable int productId) {
		log.info("ProductRestController.deleteProduct() :: tries to delete an entity with productId: {}", productId);
		productService.deleteProduct(productId);
	}

}
