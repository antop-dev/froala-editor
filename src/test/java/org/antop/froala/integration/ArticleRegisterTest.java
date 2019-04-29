package org.antop.froala.integration;

import org.antop.froala.DefaultTests;
import org.antop.froala.FroalaEditorApplication;
import org.antop.froala.entity.Article;
import org.antop.froala.service.ArticleService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = FroalaEditorApplication.class)
@AutoConfigureMockMvc
public class ArticleRegisterTest extends DefaultTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ArticleService service;

    @Test
    void inquire() throws Exception {
        // first register
        String content = faker.book().title();
        mockMvc.perform(post("/submit").param("content", content)).andExpect(status().isFound());
        // second register
        mockMvc.perform(post("/submit").param("content", faker.book().title())).andExpect(status().isFound());
        // verify
        List<Article> inquire = service.inquire();
        assertEquals(2, inquire.size());
        assertEquals(content, service.inquire(1).getContent());
    }

}
