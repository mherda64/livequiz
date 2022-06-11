package com.mherda.livequiz.exception;

public class SessionAlreadyOpenedException extends RuntimeException {
    public SessionAlreadyOpenedException(String message) {
        super(message);
    }
}
