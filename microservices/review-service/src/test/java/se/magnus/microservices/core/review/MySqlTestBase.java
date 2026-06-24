package se.magnus.microservices.core.review;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.MySQLContainer;

public abstract class MySqlTestBase {

	// The @ServiceConnection annotation will auto-configure the corresponding Spring connection properties.
	// It will, for example, set the spring.datasource.url, spring.datasource.username, and spring.datasource.password properties
	// based on the information from the database object using the getJdbcUrl(), getUsername(), and getPassword() methods.

	// Extend startup timeout since a MySQLContainer with MySQL 8 starts very slow on Win10/WSL2
	@ServiceConnection
	static final JdbcDatabaseContainer database = new MySQLContainer("mysql:9.2.0").withStartupTimeoutSeconds(300);

	static {
		database.start();
	}

}