package ru.practicum.shareit.exception;

public class IllegalStatusException extends RuntimeException {

    public IllegalStatusException() {
    }

    public IllegalStatusException(String state) {
        super("Неверное знчение параметра 'approved'=" + state);
    }
}
