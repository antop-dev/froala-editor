package org.antop.froala.unit.controller;

import org.antop.froala.DefaultTests;
import org.antop.froala.controller.IndexController;
import org.antop.froala.service.ArticleService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(IndexController.class)
class IndexControllerTest extends DefaultTests {

	@Autowired
	private MockMvc mockMvc;
	@MockBean
	private ArticleService articleService;

	@Test
	void 루트_페이지() throws Exception {
		// action
		mockMvc.perform(get("/")).andExpect(status().isOk());
		// verify
		verify(articleService).inquire();
	}

	@Test
	void 인덱스_페이지() throws Exception {
		// action
		mockMvc.perform(MockMvcRequestBuilders.get("/index.html")).andExpect(status().isOk());
		// verify
		verify(articleService).inquire();
	}

}