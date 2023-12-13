package ru.practicum.shareit.exception;

public class UnavailableException extends RuntimeException {

    public UnavailableException() {
    }

    public UnavailableException(String message) {
        super(message);
    }

    public UnavailableException(Long id) {
        super("Вещь с ID=" + id + " недоступна для бронирования.");
    }
}
