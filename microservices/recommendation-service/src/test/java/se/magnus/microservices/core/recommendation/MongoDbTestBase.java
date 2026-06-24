package se.magnus.microservices.core.recommendation;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.MongoDBContainer;

public abstract class MongoDbTestBase {

	// The @ServiceConnection annotation will auto-configure the corresponding Spring connection properties. 
	// It will, for example, set the spring.datasource.url, spring.datasource.username, and spring.datasource.password properties 
	// based on the information from the database object using the getJdbcUrl(), getUsername(), and getPassword() methods.
	@ServiceConnection
	private static MongoDBContainer database = new MongoDBContainer("mongo:8.0.5");

	static {
		database.start();
	}

}