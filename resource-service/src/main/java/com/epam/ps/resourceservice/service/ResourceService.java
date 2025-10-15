package com.epam.ps.resourceservice.service;

import com.epam.ps.resourceservice.client.SongServiceClient;
import com.epam.ps.resourceservice.dto.ResourceDto;
import com.epam.ps.resourceservice.entity.ResourceEntity;
import com.epam.ps.resourceservice.exception.ResourceNotFoundException;
import com.epam.ps.resourceservice.repository.ResourceRepository;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.tika.Tika;
import org.apache.tika.metadata.Metadata;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ResourceService {

    private final ResourceRepository resourceRepository;
    private final SongServiceClient songServiceClient;
    private final Tika tika = new Tika();

    public ResourceService(ResourceRepository resourceRepository, SongServiceClient songServiceClient) {
        this.resourceRepository = resourceRepository;
        this.songServiceClient = songServiceClient;
    }

    @Transactional
    public ResourceDto uploadResource(byte[] mp3Data) {
        try {
            if (!"audio/mpeg".equals(tika.detect(mp3Data))) {
                throw new IllegalArgumentException("Invalid MP3 file");
            }
            ResourceEntity entity = new ResourceEntity();
            entity.setData(mp3Data);
            entity = resourceRepository.save(entity);

            Metadata metadata = new Metadata();
            tika.parse(new ByteArrayInputStream(mp3Data), metadata);

            Map<String, Object> songMetadata = new HashMap<>();
            songMetadata.put("id", entity.getId());
            songMetadata.put("name", metadata.get("dc:title"));
            songMetadata.put("artist", metadata.get("xmpDM:artist"));
            songMetadata.put("album", metadata.get("xmpDM:album"));
            songMetadata.put("duration", formatDuration(metadata.get("xmpDM:duration")));
            songMetadata.put("year", extractYear(metadata.get("xmpDM:releaseDate")));

            songServiceClient.saveSongMetadata(songMetadata);

            return new ResourceDto(entity.getId());
        } catch (Exception e) {
            throw new RuntimeException("Failed to process MP3 file", e);
        }
    }

    public byte[] getResource(Long id) {
        validateId(id);
        return resourceRepository.findById(id)
                .map(ResourceEntity::getData)
                .orElseThrow(() -> new ResourceNotFoundException(id));
    }

    @Transactional
    public List<Long> deleteResourcesByCsv(String idsCsv) {
        if (idsCsv == null || idsCsv.length() > 200) {
            throw new IllegalArgumentException("CSV string format is invalid or exceeds length restrictions");
        }
        List<Long> ids = parseAndValidateIds(idsCsv);
        List<Long> deleted = new ArrayList<>();
        for (Long id : ids) {
            if (resourceRepository.existsById(id)) {
                resourceRepository.deleteById(id);
                songServiceClient.deleteSongMetadata(id); // Cascading delete
                deleted.add(id);
            }
        }
        return deleted;
    }

    private List<Long> parseAndValidateIds(String idsCsv) {
        try {
            return Arrays.stream(idsCsv.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(Long::parseLong)
                    .filter(id -> id > 0)
                    .collect(Collectors.toList());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("CSV string contains invalid IDs");
        }
    }

    private void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid ID");
        }
    }

    private String formatDuration(String durationSeconds) {
        if (durationSeconds == null) {
            return "00:00";
        }
        try {
            int seconds = (int) Double.parseDouble(durationSeconds);
            int min = seconds / 60;
            int sec = seconds % 60;
            return String.format("%02d:%02d", min, sec);
        } catch (Exception e) {
            return "00:00";
        }
    }

    private String extractYear(String releaseDate) {
        if (releaseDate == null) return "1900";
        Matcher matcher = Pattern.compile("(19|20)\\d{2}").matcher(releaseDate);
        if (matcher.find()) {
            return matcher.group();
        }
        return "1900";
    }
}

