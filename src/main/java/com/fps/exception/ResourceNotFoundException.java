package com.fps.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String entity, String id) {
        super(entity + " not found with id: " + id);
    }
}
