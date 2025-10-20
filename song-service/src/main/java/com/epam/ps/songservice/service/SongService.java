package com.epam.ps.songservice.service;

import com.epam.ps.songservice.dto.SongDto;
import com.epam.ps.songservice.entity.SongEntity;
import com.epam.ps.songservice.exception.SongAlreadyExistsException;
import com.epam.ps.songservice.exception.SongNotFoundException;
import com.epam.ps.songservice.repository.SongRepository;
import java.util.ArrayList;
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SongService {

    private final SongRepository songRepository;

    @Autowired
    public SongService(SongRepository songRepository) {
        this.songRepository = songRepository;
    }

    public SongDto getSongById(Long id) {
        validateId(id);
        SongEntity entity = songRepository.findById(id)
                .orElseThrow(() -> new SongNotFoundException(id));
        return toDto(entity);
    }

    public SongDto createSong(SongDto songDto) {
        validateId(songDto.getId());
        if (songRepository.existsById(songDto.getId())) {
            throw new SongAlreadyExistsException(songDto.getId());
        }
        SongEntity entity = toEntity(songDto);
        SongEntity saved = songRepository.save(entity);
        return toDto(saved);
    }

    public List<Long> deleteSongsByCsv(String idsCsv) {
        if (idsCsv == null) {
            throw new IllegalArgumentException("CSV string can not be null");
        }
        if (idsCsv.length() > 200) {
            throw new IllegalArgumentException("CSV string exceeds length restrictions. Max length is 200 characters. Current length: " + idsCsv.length());
        }
        List<Long> ids = parseAndValidateIds(idsCsv);
        List<Long> deleted = new ArrayList<>();
        for (Long id : ids) {
            if (songRepository.existsById(id)) {
                songRepository.deleteById(id);
                deleted.add(id);
            }
        }
        return deleted;
    }

    private List<Long> parseAndValidateIds(String idsCsv) {
        List<Long> ids = new ArrayList<>();
        for (String s : idsCsv.split(",")) {
            String trimmed = s.trim();
            if (trimmed.isEmpty()) continue;
            try {
                long id = Long.parseLong(trimmed);
                if (id <= 0) {
                    throw new IllegalArgumentException("Invalid ID: '" + trimmed + "'. Only positive integers are allowed.");
                }
                ids.add(id);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid ID format: '" + trimmed + "'. Only positive integers are allowed.");
            }
        }
        return ids;
    }

    private void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid ID");
        }
    }

    private SongDto toDto(SongEntity entity) {
        return new SongDto(
                entity.getId(),
                entity.getName(),
                entity.getArtist(),
                entity.getAlbum(),
                entity.getDuration(),
                entity.getYear()
        );
    }

    private SongEntity toEntity(SongDto dto) {
        return new SongEntity(
                dto.getId(),
                dto.getName(),
                dto.getArtist(),
                dto.getAlbum(),
                dto.getDuration(),
                dto.getYear()
        );
    }
}