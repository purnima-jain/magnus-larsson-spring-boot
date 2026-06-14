package se.magnus.microservices.core.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
// To enable Spring Boot’s autoconfiguration feature to detect Spring beans in the api and util projects, 
// we also need to add a @ComponentScan annotation to the main application class, which includes the packages of the api and util projects.
@ComponentScan("se.magnus")
@Slf4j
public class ProductServiceApplication {

	@Bean
	RestTemplate restTemplate() {
		return new RestTemplate();
	}

	public static void main(String[] args) {
		SpringApplication.run(ProductServiceApplication.class, args);
		log.info("Starting ProductServiceApplication...............");
	}

}
