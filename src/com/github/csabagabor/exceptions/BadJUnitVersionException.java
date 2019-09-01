package com.github.csabagabor.exceptions;

public class BadJUnitVersionException extends RuntimeException {
    public BadJUnitVersionException() {
    }

    public BadJUnitVersionException(String message) {
        super(message);
    }

    public BadJUnitVersionException(String message, Throwable cause) {
        super(message, cause);
    }

    public BadJUnitVersionException(Throwable cause) {
        super(cause);
    }
}
