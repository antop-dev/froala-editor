package org.antop.froala.repository;

import org.antop.froala.entity.StoredFile;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface StoredFileRepository extends CrudRepository<StoredFile, UUID> {

	@Query("select o from StoredFile o where mimeType like 'image/%' order by added desc")
	List<StoredFile> findImage();

}
