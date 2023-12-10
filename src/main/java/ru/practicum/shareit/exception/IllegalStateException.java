package ru.practicum.shareit.exception;

public class IllegalStateException extends RuntimeException {

    public IllegalStateException(String state) {
        super(String.format("Unknown state: %s", state));
    }
}
