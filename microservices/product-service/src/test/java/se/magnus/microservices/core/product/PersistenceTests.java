package se.magnus.microservices.core.product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;

import se.magnus.microservices.core.product.persistence.ProductEntity;
import se.magnus.microservices.core.product.persistence.ProductRepository;

@DataMongoTest // This annotation starts up a MongoDB database when the test starts
class PersistenceTests extends MongoDbTestBase {

	@Autowired
	private ProductRepository repository;

	private ProductEntity savedEntity;

	@BeforeEach // executed before each test method
	void setupDb() {
		repository.deleteAll();

		ProductEntity entity = new ProductEntity(1, "n", 1);
		savedEntity = repository.save(entity);

		assertEqualsProduct(entity, savedEntity);
	}

	@Test
	void create() {

		ProductEntity newEntity = new ProductEntity(2, "n", 2);
		repository.save(newEntity); // creates a new entity

		ProductEntity foundEntity = repository.findById(newEntity.getId()).get(); // verifies that the new entity can be found using the findById() method
		assertEqualsProduct(newEntity, foundEntity);

		assertEquals(2, repository.count()); // asserts that there are two entities stored in the database, the one created by the setup method and the one created by the test itself
	}

	@Test
	void update() {
		savedEntity.setName("n2");
		repository.save(savedEntity);

		ProductEntity foundEntity = repository.findById(savedEntity.getId()).get();

		assertEquals(1, (long) foundEntity.getVersion()); // Note that, when an entity is created, its version field is set to 0 by Spring Data, so we expect it to be 1 after the update.
		assertEquals("n2", foundEntity.getName());
	}

	@Test
	void delete() {
		repository.delete(savedEntity);
		assertFalse(repository.existsById(savedEntity.getId()));
	}

	@Test
	void getByProductId() {
		Optional<ProductEntity> entity = repository.findByProductId(savedEntity.getProductId());

		assertTrue(entity.isPresent());
		assertEqualsProduct(savedEntity, entity.get());
	}

//	@Test
//	void duplicateError() {
//		assertThrows(DuplicateKeyException.class, () -> {
//			ProductEntity entity = new ProductEntity(savedEntity.getProductId(), "n", 1);
//			repository.save(entity);
//		});
//	}

	// This is a test that verifies correct error handling in the case of updates of stale data—it verifies that the optimistic locking mechanism works.
	@Test
	void optimisticLockError() {

		// Store the saved entity in two separate entity objects
		ProductEntity entity1 = repository.findById(savedEntity.getId()).get();
		ProductEntity entity2 = repository.findById(savedEntity.getId()).get();

		// Update the entity using the first entity object
		entity1.setName("n1");
		repository.save(entity1); // The update of the entity in the database will cause the version field of the entity to be increased automatically by Spring Data.

		// Update the entity using the second entity object.
		// This should fail since the second entity now holds an old version number, i.e. an Optimistic Lock Error
		// entity2, now contains stale data, manifested by its version field, which holds a lower value than the corresponding value in the database.
		assertThrows(OptimisticLockingFailureException.class, () -> {
			entity2.setName("n2");
			repository.save(entity2);
		});

		// Get the updated entity from the database and verify its new sate
		ProductEntity updatedEntity = repository.findById(savedEntity.getId()).get();
		assertEquals(1, (int) updatedEntity.getVersion());
		assertEquals("n1", updatedEntity.getName());
	}

	@Test
	void paging() {

		repository.deleteAll(); // Removes all existing data

		// Inserts 10 entities with the productId field ranging from 1001 to 1010.
		List<ProductEntity> newProducts = IntStream.rangeClosed(1001, 1010).mapToObj(i -> new ProductEntity(i, "name " + i, i)).collect(Collectors.toList());
		repository.saveAll(newProducts);

		Pageable nextPage = PageRequest.of(0, 4, Direction.ASC, "productId"); // A page count of 4 entities per page and a sort order based on ProductId in ascending order.

		// Read the expected three pages, verifying the expected product IDs on each page and verifying that Spring Data correctly reports back whether more pages exist or not.
		nextPage = testNextPage(nextPage, "[1001, 1002, 1003, 1004]", true);
		nextPage = testNextPage(nextPage, "[1005, 1006, 1007, 1008]", true);
		nextPage = testNextPage(nextPage, "[1009, 1010]", false);
	}

	private Pageable testNextPage(Pageable nextPage, String expectedProductIds, boolean expectsNextPage) {
		Page<ProductEntity> productPage = repository.findAll(nextPage);
		
		assertEquals(expectedProductIds, productPage.getContent().stream().map(p -> p.getProductId()).collect(Collectors.toList()).toString());
		assertEquals(expectsNextPage, productPage.hasNext());
		
		return productPage.nextPageable(); // returns the next page
	}

	private void assertEqualsProduct(ProductEntity expectedEntity, ProductEntity actualEntity) {
		assertEquals(expectedEntity.getId(), actualEntity.getId());
		assertEquals(expectedEntity.getVersion(), actualEntity.getVersion());
		assertEquals(expectedEntity.getProductId(), actualEntity.getProductId());
		assertEquals(expectedEntity.getName(), actualEntity.getName());
		assertEquals(expectedEntity.getWeight(), actualEntity.getWeight());
	}

}
