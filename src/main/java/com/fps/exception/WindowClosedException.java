package com.fps.exception;

public class WindowClosedException extends RuntimeException {
    public WindowClosedException() {
        super("Prediction window is no longer open");
    }
}
