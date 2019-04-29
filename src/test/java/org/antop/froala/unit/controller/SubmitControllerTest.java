package org.antop.froala.unit.controller;

import org.antop.froala.DefaultTests;
import org.antop.froala.controller.SubmitController;
import org.antop.froala.service.ArticleService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(SubmitController.class)
public class SubmitControllerTest extends DefaultTests {
	@Autowired
	private MockMvc mockMvc;
	@MockBean
	private ArticleService service;

	@Test
	void submit() throws Exception {
		String content = faker.book().title();

		mockMvc.perform(post("/submit").param("content", content))
				.andDo(print())
				.andExpect(status().isFound());

		verify(service).register(eq(content));
	}

}