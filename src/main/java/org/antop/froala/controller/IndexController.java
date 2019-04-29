package org.antop.froala.controller;

import org.antop.froala.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

	@Autowired
	private ArticleService service;

	@GetMapping(value = {"", "/", "/index.html"})
	public String index(Model model) {
		model.addAttribute("items", service.inquire());
		return "index";
	}

}
