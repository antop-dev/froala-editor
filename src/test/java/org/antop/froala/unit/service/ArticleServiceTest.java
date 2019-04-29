package org.antop.froala.unit.service;

import org.antop.froala.DefaultTests;
import org.antop.froala.entity.Article;
import org.antop.froala.repository.ArticleRepository;
import org.antop.froala.service.ArticleService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ArticleService.class)
public class ArticleServiceTest extends DefaultTests {
    @MockBean
    private ArticleRepository repository;
    @Autowired
    private ArticleService service;

    @Test
    void register() {
        String content = faker.book().title();
        Article article = service.register(content);

        verify(repository).save(any(Article.class));
        assertEquals(content, article.getContent());
        assertNotNull(article.getAdded());
    }

    @Test
    void 없는_아이디로_조회시_널을_리턴() {
        // when
        doAnswer(invocation -> Optional.empty()).when(repository).findById(any());
        // action
        Article article = service.inquire(999_999_999);
        // verify
        assertNull(article);
    }
}