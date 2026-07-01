package se.magnus.microservices.core.recommendation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
//To enable Spring Boot’s autoconfiguration feature to detect Spring beans in the api and util projects, 
//we also need to add a @ComponentScan annotation to the main application class, which includes the packages of the api and util projects.
@ComponentScan("se.magnus")
@Slf4j
public class RecommendationServiceApplication {

	public static void main(String[] args) {
		// SpringApplication.run(RecommendationServiceApplication.class, args);

		ConfigurableApplicationContext ctx = SpringApplication.run(RecommendationServiceApplication.class, args);
		String mongodDbHost = ctx.getEnvironment().getProperty("spring.mongodb.host");
		String mongodDbPort = ctx.getEnvironment().getProperty("spring.mongodb.port");
		log.info("Connected to MongoDb: " + mongodDbHost + ":" + mongodDbPort);

		log.info("Starting RecommendationServiceApplication...............");
	}

}
