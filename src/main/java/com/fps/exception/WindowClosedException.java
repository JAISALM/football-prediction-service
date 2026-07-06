package com.fps.exception;

public class WindowClosedException extends RuntimeException {
    public WindowClosedException(String message) {
        super(message);
    }
}
