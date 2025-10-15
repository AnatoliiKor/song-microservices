package com.epam.ps.resourceservice.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class SongServiceClient {

    private final RestTemplate restTemplate;
    private final String songServiceUrl;

    public SongServiceClient(RestTemplate restTemplate,
                             @Value("${song.service.url}") String songServiceUrl) {
        this.restTemplate = restTemplate;
        this.songServiceUrl = songServiceUrl;
    }

    public void saveSongMetadata(Map<String, Object> metadata) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(metadata, headers);
        restTemplate.postForEntity(songServiceUrl + "/songs", request, Map.class);
    }

    public void deleteSongMetadata(Long id) {
        String url = songServiceUrl + "/songs?id=" + id;
        restTemplate.exchange(url, HttpMethod.DELETE, null, Map.class);
    }
}

