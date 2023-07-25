package org.example.server;

public class ExitException extends RuntimeException {
    public ExitException(String message) {
        super(message);
    }
}
