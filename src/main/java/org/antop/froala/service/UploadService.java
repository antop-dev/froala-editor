package org.antop.froala.service;

import lombok.extern.slf4j.Slf4j;
import org.antop.froala.entity.StoredFile;
import org.antop.froala.model.FileInfo;
import org.antop.froala.repository.StoredFileRepository;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static java.util.stream.Collectors.toMap;

@Service
@Slf4j
public class UploadService {
	private final Environment env;
	private final StoredFileRepository repository;

	public UploadService(Environment env, StoredFileRepository repository) {
		this.env = env;
		this.repository = repository;
	}

	@Transactional
	public UUID upload(FileInfo file) throws IOException {
		String destPath = env.getRequiredProperty("multipart.upload.path");
		destPath += "/" + UUID.randomUUID().toString();

		String extension = FilenameUtils.getExtension(file.getName());
		if (StringUtils.isNotEmpty(extension)) {
			destPath += "." + extension;
		}

		// upload
		Path path = Paths.get(destPath);
		try (OutputStream out = Files.newOutputStream(path)) {
			FileCopyUtils.copy(file.getBytes(), out);

			StoredFile storedFile = new StoredFile();
			storedFile.setOriginalName(file.getName());
			storedFile.setRealPath(destPath);
			storedFile.setMimeType(file.getMimeType());
			storedFile.setSize(file.getSize());
			storedFile.setAdded(LocalDateTime.now());
			repository.save(storedFile);

			return storedFile.getId();
		}
	}

	public FileInfo get(UUID uuid) {
		Optional<StoredFile> optional = repository.findById(uuid);
		return optional.filter(storedFile -> isExists(storedFile.getRealPath()))
				.map(storedFile -> createFileInfo(storedFile, true))
				.orElse(null);
	}

	public Map<UUID, FileInfo> inquireImages() {
		List<StoredFile> find = repository.findImage();
		return find.stream().filter(r -> isExists(r.getRealPath()))
				.collect(toMap(StoredFile::getId, storedFile -> createFileInfo(storedFile, false)));
	}

	public void remove(final UUID uuid) {
		Optional<StoredFile> optional = repository.findById(uuid);
		optional.ifPresent(m -> {
			try {
				Files.deleteIfExists(Paths.get(m.getRealPath()));
				repository.deleteById(uuid);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});
	}

	/**
	 * 데이터베이스 레코드로 파일 정보를 만든다.
	 *
	 * @param record   데이터베이스 레코드
	 * @param readByte 내용(byte[])을 읽을지 여부
	 * @return 파일 정보
	 */
	private FileInfo createFileInfo(StoredFile record, boolean readByte) {
		try {
			Path path = Paths.get(record.getRealPath());
			FileInfo fileInfo = new FileInfo(record.getOriginalName());
			fileInfo.setMimeType(record.getMimeType());
			fileInfo.setSize(record.getSize());
			if (readByte) {
				fileInfo.setBytes(Files.readAllBytes(path));
			}
			return fileInfo;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 문자 경로로 실제 파일이 존재하는지 여부
	 *
	 * @param realPath 실제 파일 위치
	 * @return 존재 여부
	 */
	private boolean isExists(String realPath) {
		return Paths.get(realPath).toFile().exists();
	}

	@PostConstruct
	public void init() throws IOException {
		String rootPath = env.getRequiredProperty("multipart.upload.path");
		log.debug("upload root path = {}", rootPath);
		Path path = Paths.get(rootPath);
		if (!path.toFile().exists()) {
			Files.createDirectory(path);
		}
	}

}
