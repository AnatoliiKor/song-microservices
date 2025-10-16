package com.epam.ps.resourceservice.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(Long id) {
        super("Resource with ID=" + id + " not found");
    }
}