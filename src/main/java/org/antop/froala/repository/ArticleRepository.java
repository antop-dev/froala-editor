package org.antop.froala.repository;

import org.antop.froala.entity.Article;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ArticleRepository extends CrudRepository<Article, Long> {

	List<Article> findAllByOrderByIdDesc();

}
