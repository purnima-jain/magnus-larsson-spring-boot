package se.magnus.microservices.composite.product.domain.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;
import se.magnus.api.rest.exceptions.InvalidInputException;
import se.magnus.api.rest.exceptions.NotFoundException;
import se.magnus.product.api.rest.response.ProductResponseDto;
import se.magnus.recommendation.api.rest.response.RecommendationResponseDto;
import se.magnus.review.api.rest.response.ReviewResponseDto;
import se.magnus.util.http.HttpErrorInfo;
import tools.jackson.databind.ObjectMapper;

@Service
@Slf4j
public class ProductCompositeIntegrationService {

	// We are using RestTemplate here instead of Reactive WebClient as that would require would require all source code where WebClient is used to also be reactive, 
	// including the declaration of the RESTful API in the API project and the source code in the composite microservice. 
	private final RestTemplate restTemplate;
	
	// Used for accessing error messages in case of errors
	private final ObjectMapper mapper;

	private final String productServiceUrl;
	private final String recommendationServiceUrl;
	private final String reviewServiceUrl;

	public ProductCompositeIntegrationService(RestTemplate restTemplate, 
												ObjectMapper mapper, 
												@Value("${app.product-service.host}") String productServiceHost,
												@Value("${app.product-service.port}") int productServicePort, 
												@Value("${app.recommendation-service.host}") String recommendationServiceHost,
												@Value("${app.recommendation-service.port}") int recommendationServicePort, 
												@Value("${app.review-service.host}") String reviewServiceHost,
												@Value("${app.review-service.port}") int reviewServicePort) {

		this.restTemplate = restTemplate;
		this.mapper = mapper;

		productServiceUrl = "http://" + productServiceHost + ":" + productServicePort + "/product/";
		recommendationServiceUrl = "http://" + recommendationServiceHost + ":" + recommendationServicePort + "/recommendation?productId=";
		reviewServiceUrl = "http://" + reviewServiceHost + ":" + reviewServicePort + "/review?productId=";
	}

	public ProductResponseDto getProduct(int productId) {

		try {
			String url = productServiceUrl + productId;
			log.debug("Will call getProduct API on URL: {}", url);

			// For the getProduct() implementation, the getForObject() method can be used in RestTemplate. 
			// The expected response is a Product object. 
			// It can be expressed in the call to getForObject() by specifying the Product.class class that RestTemplate will map the JSON response to.
			ProductResponseDto productResponseDto = restTemplate.getForObject(url, ProductResponseDto.class);
			log.debug("Found a product with id: {}", productResponseDto.getProductId());

			return productResponseDto;

		} catch (HttpClientErrorException ex) {

			switch (HttpStatus.resolve(ex.getStatusCode().value())) {
			case NOT_FOUND:
				throw new NotFoundException(getErrorMessage(ex));

			case UNPROCESSABLE_ENTITY:
				throw new InvalidInputException(getErrorMessage(ex));

			default:
				log.warn("Got an unexpected HTTP error: {}, will rethrow it", ex.getStatusCode());
				log.warn("Error body: {}", ex.getResponseBodyAsString());
				throw ex;
			}
		}
	}

	private String getErrorMessage(HttpClientErrorException ex) {
		return mapper.readValue(ex.getResponseBodyAsString(), HttpErrorInfo.class).getMessage();
	}

	// For the calls to getRecommendations() and getReviews(), a more advanced method, exchange(), has to be used. 
	// The reason for this is the automatic mapping from a JSON response to a model class that RestTemplate performs. 
	// The getRecommendations() and getReviews() methods expect a generic list in the responses, that is, List<Recommendation> and List<Review>. 
	// Since generics don’t hold any type of information at runtime, we can’t specify that the methods expect a generic list in their responses. 
	// Instead, we can use a helper class from the Spring Framework, ParameterizedTypeReference, which is designed to resolve this problem by holding the type information at runtime. 
	// This means that RestTemplate can figure out what class to map the JSON responses to. 
	// To utilize this helper class, we have to use the more involved exchange() method instead of the simpler getForObject() method on RestTemplate.
	public List<RecommendationResponseDto> getRecommendations(int productId) {

		try {
			String url = recommendationServiceUrl + productId;

			log.debug("Will call getRecommendations API on URL: {}", url);
			List<RecommendationResponseDto> recommendationResponseDtoList = restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<RecommendationResponseDto>>() {
			}).getBody();

			log.debug("Found {} recommendations for a product with id: {}", recommendationResponseDtoList.size(), productId);
			return recommendationResponseDtoList;

		} catch (Exception ex) {
			log.warn("Got an exception while requesting recommendations, return zero recommendations: {}", ex.getMessage());
			return new ArrayList<>();
		}
	}

	public List<ReviewResponseDto> getReviews(int productId) {

		try {
			String url = reviewServiceUrl + productId;

			log.debug("Will call getReviews API on URL: {}", url);
			List<ReviewResponseDto> reviewResponseDtoList = restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<ReviewResponseDto>>() {
			}).getBody();

			log.debug("Found {} reviews for a product with id: {}", reviewResponseDtoList.size(), productId);
			return reviewResponseDtoList;

		} catch (Exception ex) {
			log.warn("Got an exception while requesting reviews, return zero reviews: {}", ex.getMessage());
			return new ArrayList<>();
		}
	}

}
