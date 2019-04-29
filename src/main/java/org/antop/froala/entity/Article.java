package org.antop.froala.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@ToString
@Entity
public class Article {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "content", columnDefinition = "MEDIUMTEXT", nullable = false)
	private String content;
	@Column(nullable = false)
	private LocalDateTime added;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Article article = (Article) o;
		return Objects.equals(id, article.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
	
}
