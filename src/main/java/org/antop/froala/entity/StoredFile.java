package org.antop.froala.entity;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
public class StoredFile {
	@Id
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "uuid2")
	private UUID id;
	@Column(nullable = false)
	private String originalName;
	@Column(nullable = false)
	private String realPath;
	@Column(nullable = false)
	private String mimeType;
	@Column(nullable = false)
	private long size;
	@Column(nullable = false)
	private LocalDateTime added;

}