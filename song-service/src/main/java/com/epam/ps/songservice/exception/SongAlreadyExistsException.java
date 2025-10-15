package com.epam.ps.songservice.exception;

public class SongAlreadyExistsException extends RuntimeException {

    public SongAlreadyExistsException(Long id) {
        super("Metadata for this ID already exists: " + id);
    }
}