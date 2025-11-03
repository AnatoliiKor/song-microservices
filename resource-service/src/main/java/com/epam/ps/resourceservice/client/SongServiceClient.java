package com.epam.ps.resourceservice.client;

import java.util.Map;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class SongServiceClient {

    private static final String HTTP_SONG_SERVICE_SONGS = "http://song-service/songs";
    private final RestTemplate restTemplate;

    public SongServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void saveSongMetadata(Map<String, Object> metadata) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(metadata, headers);
        restTemplate.postForEntity(HTTP_SONG_SERVICE_SONGS, request, Map.class);
    }

    public void deleteSongMetadata(Long id) {
        String url = HTTP_SONG_SERVICE_SONGS + "?id=" + id;
        restTemplate.exchange(url, HttpMethod.DELETE, null, Map.class);
    }
}

