package com.epam.ps.songservice.controller;

import com.epam.ps.songservice.dto.SongDto;
import com.epam.ps.songservice.service.SongService;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/songs")
public class SongController {

    private final SongService songService;

    public SongController(SongService songService) {
        this.songService = songService;
    }

    @PostMapping
    public ResponseEntity<?> createSong(@Valid @RequestBody SongDto songDto) {
        SongDto created = songService.createSong(songDto);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SongDto> getSong(@PathVariable Long id) {
        SongDto song = songService.getSongById(id);
        return ResponseEntity.ok(song);
    }

    @DeleteMapping
    public ResponseEntity<Map<String, List<Long>>> deleteSongs(@RequestParam("id") String idsCsv) {
        List<Long> deleted = songService.deleteSongsByCsv(idsCsv);
        return ResponseEntity.ok(Map.of("ids", deleted));
    }
}