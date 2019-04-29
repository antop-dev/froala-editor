package org.antop.froala.unit.controller;

import com.github.javafaker.File;
import org.antop.froala.DefaultTests;
import org.antop.froala.controller.FroalaController;
import org.antop.froala.model.FileInfo;
import org.antop.froala.service.UploadService;
import org.apache.tika.Tika;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.UUID.randomUUID;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(FroalaController.class)
public class FroalaControllerTest extends DefaultTests {
	@Autowired
	private MockMvc mockMvc;
	@MockBean
	private UploadService service;

	@Test
	void 이미지_업로드() throws Exception {
		final UUID uuid = UUID.randomUUID();
		// when
		when(service.upload(any(FileInfo.class))).thenReturn(uuid);
		// action
		MockMultipartFile multipart = createMultipart("file", "assets/image/image-1.png");
		ResultActions action = mockMvc.perform(multipart("/froala/image").file(multipart)).andDo(print());
		// verify
		action.andExpect(status().isOk())
				.andExpect(jsonPath("$.link", is("/froala/" + uuid.toString())));

		ArgumentCaptor<FileInfo> arguments = ArgumentCaptor.forClass(FileInfo.class);
		verify(service).upload(arguments.capture());

		assertEquals(multipart.getOriginalFilename(), arguments.getValue().getName());
		assertArrayEquals(multipart.getBytes(), arguments.getValue().getBytes());
		assertEquals(multipart.getContentType(), arguments.getValue().getMimeType());
	}

	@Test
	void 다운로드_한다() throws Exception {
		Path path = Paths.get("assets/image/image-1.png");
		FileInfo fileInfo = new FileInfo(path.getFileName().toString(), Files.readAllBytes(path));
		// when
		when(service.get(any(UUID.class))).thenReturn(fileInfo);
		// action
		ResultActions action = mockMvc.perform(get("/froala/{uuid}", randomUUID().toString())).andDo(print());
		//verify
		action.andExpect(status().isOk());
	}

	@Test
	void 이미지가_아닌_파일을_업로드_하면_400() throws Exception {
		// 확장자는 이미지이나 실체는 이미지 파일이 아니다.
		MockMultipartFile multipart = createMultipart("file", "assets/image/no-image.png");
		// action
		ResultActions action = mockMvc.perform(multipart("/froala/image").file(multipart)).andDo(print());
		// verify
		action.andExpect(status().isBadRequest());
	}

	@Test
	void 잘못된_파라미터명으로_업로드시_400() throws Exception {
		String name = "attach"; // file로 업로드 해야한다.
		MockMultipartFile multipart = createMultipart(name, "assets/image/image-1.png");
		// action
		ResultActions action = mockMvc.perform(multipart("/froala/image").file(multipart)).andDo(print());
		// verify
		action.andExpect(status().isBadRequest());
	}

	@Test
	void 파일이_아닌_파라미터를_넘기면_400() throws Exception {
		// action
		ResultActions action = mockMvc.perform(multipart("/froala/image").param("file", "no file")).andDo(print());
		// verify
		action.andExpect(status().isBadRequest());
	}

	@Test
	void 없는_파일_다운로드시_404() throws Exception {
		// when
		when(service.get(any())).thenReturn(null);
		// action
		ResultActions action = mockMvc.perform(get("/froala/{uuid}", randomUUID().toString())).andDo(print());
		// verify
		action.andExpect(status().isNotFound());
	}

	@Test
	void 파일_업로드() throws Exception {
		// when
		when(service.upload(any(FileInfo.class))).thenReturn(randomUUID());
		// file
		MockMultipartFile multipart = createMultipart("file", "assets/file/goodbyedpi-0.1.5.zip");
		// action
		ResultActions action = mockMvc.perform(multipart("/froala/file").file(multipart)).andDo(print());
		// verify
		action.andExpect(status().isOk());
		ArgumentCaptor<FileInfo> arguments = ArgumentCaptor.forClass(FileInfo.class);
		verify(service).upload(arguments.capture());

		assertEquals(multipart.getOriginalFilename(), arguments.getValue().getName());
		assertArrayEquals(multipart.getBytes(), arguments.getValue().getBytes());
		assertEquals(multipart.getContentType(), arguments.getValue().getMimeType());
	}

	@Test
	void 확장자를_다르게_올려도_마임타입을_찾는다() throws Exception {
		// when
		when(service.upload(any(FileInfo.class))).thenReturn(randomUUID());
		// file
		MockMultipartFile multipart = createMultipart("file", "assets/file/image.txt");
		// action
		ResultActions action = mockMvc.perform(multipart("/froala/file").file(multipart)).andDo(print());
		// verify
		action.andExpect(status().isOk());
		ArgumentCaptor<FileInfo> arguments = ArgumentCaptor.forClass(FileInfo.class);
		verify(service).upload(arguments.capture());
		// 파일명은 txt지만 실제 파일 내용은 jpeg이다.
		assertEquals("image/jpeg", arguments.getValue().getMimeType());
	}

	@Test
	void 이미지_매니저_목록을_조회한다() throws Exception {
		when(service.inquireImages()).thenReturn(createImageFileStubList());
		// action
		ResultActions action = mockMvc.perform(get("/froala/images")).andDo(print());
		// verify
		action.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(5)));
	}

	@Test
	void 업로드된_파일을_삭제한다() throws Exception {
		UUID uuid = randomUUID();
		// action
		ResultActions action = mockMvc.perform(delete("/froala/{uuid}", uuid.toString())).andDo(print());
		// verify
		action.andExpect(status().isOk());
		verify(service).remove(eq(uuid));
	}

	@Test
	void 비디오_업로드() throws Exception {
		// when
		when(service.upload(any(FileInfo.class))).thenReturn(randomUUID());
		// file
		MockMultipartFile multipart = createMultipart("file", "assets/video/bear_in_the_woods.webm");
		// action
		ResultActions action = mockMvc.perform(multipart("/froala/video").file(multipart)).andDo(print());
		// verify
		action.andExpect(status().isOk());
		ArgumentCaptor<FileInfo> arguments = ArgumentCaptor.forClass(FileInfo.class);
		verify(service).upload(arguments.capture());

		assertEquals(multipart.getOriginalFilename(), arguments.getValue().getName());
		assertArrayEquals(multipart.getBytes(), arguments.getValue().getBytes());
		assertEquals(multipart.getContentType(), arguments.getValue().getMimeType());
	}

	/**
	 * 랜덤 5개 파일정보 리턴
	 *
	 * @return 리스트 5개
	 */
	private Map<UUID, FileInfo> createImageFileStubList() {
		return IntStream.range(0, 5).mapToObj(i -> {
			File file = faker.file();
			FileInfo fileInfo = new FileInfo(file.fileName("", null, null, "") + "." + file.extension());
			fileInfo.setMimeType(file.mimeType());
			fileInfo.setSize(faker.number().randomNumber());
			return fileInfo;
		}).collect(Collectors.toMap(t -> randomUUID(), t -> t));
	}

	private MockMultipartFile createMultipart(String name, String realPath) throws IOException {
		// application/zip
		Path path = Paths.get(realPath);
		byte[] bytes = Files.readAllBytes(path);
		// mime type
		Tika tika = new Tika();
		String mimeType = tika.detect(bytes, path.getFileName().toString());
		// mock multipart
		return new MockMultipartFile(name, path.getFileName().toString(), mimeType, bytes);
	}

}
