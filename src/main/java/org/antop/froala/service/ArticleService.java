package org.antop.froala.service;

import org.antop.froala.entity.Article;
import org.antop.froala.repository.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ArticleService {

	@Autowired
	private ArticleRepository repository;

	public List<Article> inquire() {
		return repository.findAllByOrderByIdDesc();
	}

	@Transactional
	public Article register(String content) {
		Article article = new Article();
		article.setContent(content);
		article.setAdded(LocalDateTime.now());
		repository.save(article);
		return article;
	}

	public Article inquire(long id) {
		Optional<Article> optional = repository.findById(id);
		return optional.isPresent() ? optional.get() : null;
	}

}
