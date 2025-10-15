package com.epam.ps.resourceservice.controller;

import com.epam.ps.resourceservice.dto.ResourceDto;
import com.epam.ps.resourceservice.service.ResourceService;
import java.util.List;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/resources")
public class ResourceController {

    private static final String AUDIO_MPEG = "audio/mpeg";
    private final ResourceService resourceService;

    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @PostMapping(consumes = AUDIO_MPEG, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResourceDto> uploadResource(@RequestBody byte[] mp3Data) {
        ResourceDto dto = resourceService.uploadResource(mp3Data);
        return ResponseEntity.ok(dto);
    }

    @GetMapping(value = "/{id}", produces = AUDIO_MPEG)
    public ResponseEntity<byte[]> getResource(@PathVariable Long id) {
        byte[] data = resourceService.getResource(id);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(AUDIO_MPEG))
                .body(data);
    }

    @DeleteMapping
    public ResponseEntity<Map<String, List<Long>>> deleteResources(@RequestParam("id") String idsCsv) {
        List<Long> deleted = resourceService.deleteResourcesByCsv(idsCsv);
        return ResponseEntity.ok(Map.of("ids", deleted));
    }
}
