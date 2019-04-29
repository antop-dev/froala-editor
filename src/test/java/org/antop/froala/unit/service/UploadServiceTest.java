package org.antop.froala.unit.service;

import org.antop.froala.DefaultTests;
import org.antop.froala.entity.StoredFile;
import org.antop.froala.model.FileInfo;
import org.antop.froala.repository.StoredFileRepository;
import org.antop.froala.service.UploadService;
import org.apache.tika.Tika;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = UploadService.class)
public class UploadServiceTest extends DefaultTests {

    @MockBean
    private StoredFileRepository repository;
    @Autowired
    private UploadService service;

    @Test
    void upload() throws IOException {
        doAnswer(invocation -> {
            StoredFile sf = invocation.getArgument(0);
            sf.setId(UUID.randomUUID());
            return sf;
        }).when(repository).save(any());

        FileInfo file = createFileInfoStub();
        UUID uuid = service.upload(file);
        // verify
        assertNotNull(uuid);
        verify(repository).save(any(StoredFile.class));
    }

    @Test
    void get() throws IOException {
        Path path = Paths.get("assets/image/image-2.png");
        // mime type
        Tika tika = new Tika();
        String mimeType = tika.detect(path);
        // bytes
        byte[] bytes = Files.readAllBytes(path);
        // when
        doAnswer(invocation -> {
            StoredFile sf = new StoredFile();
            sf.setId(invocation.getArgument(0));
            sf.setOriginalName(path.getFileName().toString());
            sf.setMimeType(mimeType);
            sf.setRealPath(path.toRealPath().toString());
            sf.setSize(bytes.length);
            return Optional.of(sf);
        }).when(repository).findById(any(UUID.class));
        // action
        FileInfo fileInfo = service.get(UUID.randomUUID());
        // verify
        assertEquals(path.getFileName().toString(), fileInfo.getName());
        assertEquals(mimeType, fileInfo.getMimeType());
        assertArrayEquals(bytes, fileInfo.getBytes());
        assertEquals(bytes.length, fileInfo.getSize());
    }

    @Test
    void 파일_정보가_디비에_없으면_널을_리턴() {
        // when
        doAnswer(invocation -> Optional.empty()).when(repository).findById(any());
        // action
        FileInfo fileInfo = service.get(UUID.randomUUID());
        // verify
        Assertions.assertNull(fileInfo);
    }

    @Test
    void 실제_파일이_없을_경우_널을_리턴() {
        // when
        doAnswer(invocation -> {
            StoredFile sf = new StoredFile();
            sf.setRealPath("/tmp/" + faker.number().digits(30)); // 없는 경로
            return Optional.of(sf);
        }).when(repository).findById(any());
        // action
        FileInfo fileInfo = service.get(UUID.randomUUID());
        // verify
        Assertions.assertNull(fileInfo);
    }

    @Test
    void 이미지_파일_목록을_조회한다() {
        // "image-1"~"image-10" 까지 디비에서 조회 됨
        // 하지만 존재하는 파일은 1~3까지 3개
        when(repository.findImage()).thenReturn(createStoredFileStubList());
        // action
        Map<UUID, FileInfo> result = service.inquireImages();
        // verify
        assertEquals(3, result.size());
    }

    @Test
    void 파일을_삭제한다() throws Exception {
        // prepare
        Path from = Paths.get("assets/image/image-1.png");
        Path to = Paths.get("assets/image/image-999.png");
        FileCopyUtils.copy(from.toFile(), to.toFile());
        // when
        when(repository.findById(any())).thenReturn(Optional.of(createStoredFileStub(999)));
        // action
        UUID uuid = UUID.randomUUID();
        service.remove(uuid);
        // verify
        verify(repository).findById(eq(uuid));
        verify(repository).deleteById(eq(uuid));
        // clean
        Files.deleteIfExists(to);
    }

    @Test
    void 데이터베이스에_없는_파일은_삭제하지_않는다() {
        // when
        when(repository.findById(any())).thenReturn(Optional.empty());
        // action
        UUID uuid = UUID.randomUUID();
        service.remove(uuid);
        // verify
        verify(repository).findById(eq(uuid));
        verify(repository, never()).deleteById(eq(uuid));
    }

    private List<StoredFile> createStoredFileStubList() {
        return IntStream.range(0, 10).mapToObj(this::createStoredFileStub).collect(toList());
    }

    private StoredFile createStoredFileStub(int number) {
        StoredFile st = new StoredFile();
        st.setId(UUID.randomUUID());
        st.setOriginalName("image-" + number + ".png");
        st.setMimeType("image/png");
        st.setRealPath("assets/image/image-" + number + ".png");
        st.setAdded(LocalDateTime.now());
        return st;
    }

    private FileInfo createFileInfoStub() throws IOException {
        Path path = Paths.get("assets/image/image-2.png");
        return new FileInfo(path.getFileName().toString(), Files.readAllBytes(path));
    }
}
