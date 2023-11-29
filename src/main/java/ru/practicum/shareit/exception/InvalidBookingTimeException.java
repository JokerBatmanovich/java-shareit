package ru.practicum.shareit.exception;

public class InvalidBookingTimeException extends RuntimeException {

    public InvalidBookingTimeException() {
    }

    public InvalidBookingTimeException(String message) {
        super(message);
    }
}
