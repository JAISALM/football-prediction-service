package com.fps.exception;

public class DuplicatePredictionException extends RuntimeException {
    public DuplicatePredictionException() {
        super("User has already predicted for this window");
    }
}
