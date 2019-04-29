package org.antop.froala.integration;

import org.antop.froala.FroalaEditorApplication;
import org.antop.froala.entity.StoredFile;
import org.antop.froala.repository.StoredFileRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = FroalaEditorApplication.class)
@AutoConfigureMockMvc
public class FileRemoveFailTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StoredFileRepository repository;

    @Test
    void 파일_삭제시_에러가_발생한다() throws Exception {
        UUID uuid = UUID.randomUUID();
        StoredFile entity = new StoredFile();
        entity.setId(uuid);
        entity.setRealPath("/anyway/anything.exe");
        // when
        when(repository.findById(any())).thenReturn(Optional.of(entity));
        doThrow(RuntimeException.class).when(repository).deleteById(any());
        // action
        ResultActions action = mockMvc.perform(delete("/froala/{uuid}", uuid.toString())).andDo(print());
        // verify
        action.andExpect(status().isInternalServerError());
    }
}
