package org.antop.froala.unit.entity;

import com.jparams.verifier.tostring.ToStringVerifier;
import org.antop.froala.DefaultTests;
import org.antop.froala.entity.Article;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ArticleTest extends DefaultTests {

	@Test
	void hasString() {
		ToStringVerifier.forClass(Article.class).verify();
	}

	@Test
	void 아이디_값이_같으면_두_객체는_같은_객체이다() {
		Article article1 = new Article();
		article1.setId(10L);
		article1.setContent(faker.book().title());

		Article article2 = new Article();
		article2.setId(20L - 10);
		article2.setContent(faker.book().title());

		assertEquals(article1.getId().longValue(), article2.getId().longValue());
		assertEquals(article1, article2);
		assertEquals(article1.hashCode(), article2.hashCode());
	}

}