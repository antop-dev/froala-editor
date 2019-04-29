package org.antop.froala.controller;

import org.antop.froala.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SubmitController {

	@Autowired
	private ArticleService service;

	@PostMapping(value = "/submit")
	public String submit(@RequestParam("content") String content) {
		service.register(content);
		return "redirect:/";
	}

}
