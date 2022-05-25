package com.mherda.livequiz.session;

public class NoOpenSessionException extends RuntimeException {
    public NoOpenSessionException(String message) {
        super(message);
    }
}
