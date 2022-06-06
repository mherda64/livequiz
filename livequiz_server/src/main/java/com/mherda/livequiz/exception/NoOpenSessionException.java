package com.mherda.livequiz.exception;

public class NoOpenSessionException extends RuntimeException {
    public NoOpenSessionException(String message) {
        super(message);
    }
}
