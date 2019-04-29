package org.antop.froala.unit.repository;

import com.google.common.collect.Iterables;
import org.antop.froala.DefaultTests;
import org.antop.froala.entity.Article;
import org.antop.froala.repository.ArticleRepository;
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
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@EntityScan("org.antop.froala.entity")
@EnableJpaRepositories("org.antop.froala.repository")
@Transactional
@Rollback
public class ArticleRepositoryTest extends DefaultTests {
	@Autowired
	private ArticleRepository repository;
	@Autowired
	private EntityManager entityManager;

	@BeforeEach
	void init() {
		// init table
		Query query = entityManager.createNativeQuery("delete from article");
		query.executeUpdate();
		entityManager.flush();
	}

	@Test
	void findAll() {
		List<Article> result = repository.findAllByOrderByIdDesc();
		assertEquals(0, result.size());

		repository.save(createArticle());
		repository.save(createArticle());
		repository.save(createArticle());

		Iterable<Article> articles = repository.findAll();
		assertEquals(3, Iterables.size(articles));
	}

	private Article createArticle() {
		Article article = new Article();
		article.setContent(faker.book().title());
		article.setAdded(LocalDateTime.now());
		return article;
	}

	@Test
	void save() {
		Article article = new Article();
		article.setContent(faker.book().title());
		article.setAdded(LocalDateTime.now());
		repository.save(article);
		// verify
		assertNotNull(article.getId());
	}

}