package se.magnus.microservices.core.product.rest.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import se.magnus.microservices.core.product.domain.model.Product;
import se.magnus.microservices.core.product.domain.service.ProductService;
import se.magnus.product.api.rest.response.ProductResponseDto;
import se.magnus.util.http.ServiceUtil;

@RestController
@Slf4j
public class ProductRestController {

	private final ServiceUtil serviceUtil;

	private final ProductService productService;

	public ProductRestController(ProductService productService, ServiceUtil serviceUtil) {
		this.productService = productService;
		this.serviceUtil = serviceUtil;
	}

	@GetMapping(value = "/product/{productId}", produces = "application/json")
	public ProductResponseDto getProduct(@PathVariable int productId) {
		log.debug("/product return the found product for productId={}", productId);

		Product product = productService.getProduct(productId);

		ProductResponseDto productResponseDto = new ProductResponseDto(product.getProductId(), product.getName(), product.getWeight(), serviceUtil.getServiceAddress());

		return productResponseDto;

	}

}
