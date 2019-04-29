package org.antop.froala.controller;

import com.google.common.collect.Maps;
import org.antop.froala.dto.FroalaImage;
import org.antop.froala.model.FileInfo;
import org.antop.froala.service.UploadService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/froala")
public class FroalaController {

    private final UploadService uploadService;

    public FroalaController(UploadService uploadService) {
        this.uploadService = uploadService;
    }

    @PostMapping(value = "/file")
    @ResponseBody
    public ResponseEntity<Map<String, String>> file(@RequestParam("file") MultipartFile multipart, HttpServletRequest request) throws IOException {
        FileInfo fileInfo = new FileInfo(multipart.getOriginalFilename(), multipart.getBytes());
        // cant upload anything
        UUID uuid = uploadService.upload(fileInfo);
        // return
        Map<String, String> map = Maps.newHashMap();
        map.put("id", uuid.toString());
        map.put("link", makeLink(request, uuid));
        return ResponseEntity.ok(map);
    }

    @PostMapping(value = "/image")
    @ResponseBody
    public ResponseEntity<Map<String, String>> image(@RequestParam("file") MultipartFile multipart, HttpServletRequest request) throws IOException {
        FileInfo fileInfo = new FileInfo(multipart.getOriginalFilename(), multipart.getBytes());
        // verify image type
        if (!fileInfo.isImage()) {
            return ResponseEntity.badRequest().build();
        }
        // upload
        UUID uuid = uploadService.upload(fileInfo);
        // return
        Map<String, String> map = Maps.newHashMap();
        map.put("id", uuid.toString()); // <img data-uuid="{value}" />
        map.put("link", makeLink(request, uuid));
        return ResponseEntity.ok().body(map);
    }

    @PostMapping(value = "/video")
    @ResponseBody
    public ResponseEntity<Map<String, String>> video(@RequestParam("file") MultipartFile multipart, HttpServletRequest request) throws IOException {
        FileInfo fileInfo = new FileInfo(multipart.getOriginalFilename(), multipart.getBytes());
        // verify video type
        if (!fileInfo.isVideo()) {
            return ResponseEntity.badRequest().build();
        }
        // upload
        UUID uuid = uploadService.upload(fileInfo);
        // return
        Map<String, String> map = Maps.newHashMap();
        map.put("id", uuid.toString());
        map.put("link", makeLink(request, uuid));
        return ResponseEntity.ok().body(map);
    }

    @GetMapping(value = "/{uuid}")
    public ResponseEntity<ByteArrayResource> get(@PathVariable UUID uuid) {
        FileInfo file = uploadService.get(uuid);
        if (file == null) {
            return ResponseEntity.notFound().build();
        }

        ByteArrayResource resource = new ByteArrayResource(file.getBytes());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + file.getName())
                .contentType(MediaType.parseMediaType(file.getMimeType())).contentLength(file.getSize())
                .body(resource);
    }

    @GetMapping(value = "/images")
    public ResponseEntity<List<FroalaImage>> images(HttpServletRequest request) {
        Map<UUID, FileInfo> map = uploadService.inquireImages();

        List<FroalaImage> collect = map.entrySet().stream().map(e -> {
            UUID id = e.getKey();
            FileInfo info = e.getValue();

            FroalaImage froala = new FroalaImage();
            froala.setId(id.toString());
            froala.setName(info.getName());
            froala.setUrl(makeLink(request, id));
            froala.setThumb(makeLink(request, id));
            return froala;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(collect);
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> remove(@PathVariable UUID uuid) {
        try {
            uploadService.remove(uuid);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }



    private String getControllerMapping() {
        return this.getClass().getAnnotation(RequestMapping.class).value()[0];
    }

    private String makeLink(HttpServletRequest request, UUID uuid) {
        return request.getContextPath() + getControllerMapping() + "/" + uuid.toString();
    }

}
