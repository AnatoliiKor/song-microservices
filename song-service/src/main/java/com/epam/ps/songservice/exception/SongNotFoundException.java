package com.epam.ps.songservice.exception;

public class SongNotFoundException extends RuntimeException {

    public SongNotFoundException(Long id) {
        super("Song not found with id: " + id);
    }

    public SongNotFoundException(String message) {
        super(message);
    }}