package org.antop.froala.unit.repository;

import com.github.javafaker.File;
import org.antop.froala.DefaultTests;
import org.antop.froala.entity.StoredFile;
import org.antop.froala.repository.StoredFileRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@EntityScan("org.antop.froala.entity")
@EnableJpaRepositories("org.antop.froala.repository")
@Transactional
@Rollback
class StoredFileRepositoryTest extends DefaultTests {
	@Autowired
	private StoredFileRepository repository;
	@Autowired
	private EntityManager entityManager;

	@BeforeEach
	void init() {
		// init table
		Query query = entityManager.createNativeQuery("delete from stored_file");
		query.executeUpdate();
		entityManager.flush();
	}

	@Test
	void save() {
		StoredFile model = createStoredFile();
		// action
		repository.save(model);
		entityManager.flush();
		// verify
		Assertions.assertNotNull(model.getId());
	}

	@Test
	void 아이디를_제외한_다른_속성들은_널이_인서트될_수_없다() {
		Assertions.assertThrows(PersistenceException.class, () -> {
			StoredFile model = createStoredFile();
			model.setAdded(null);
			// action
			repository.save(model);
			entityManager.flush();
		});
	}

	private StoredFile createStoredFile() {
		File f = faker.file();

		StoredFile model = new StoredFile();
		model.setOriginalName(f.fileName("", null, null, ""));
		model.setRealPath("/upload/" + UUID.randomUUID().toString() + "." + f.extension());
		model.setMimeType(f.mimeType());
		model.setSize(faker.number().randomNumber());
		model.setAdded(LocalDateTime.now());
		return model;
	}
}