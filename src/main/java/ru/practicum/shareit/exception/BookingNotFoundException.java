package ru.practicum.shareit.exception;

public class BookingNotFoundException extends RuntimeException {
    public BookingNotFoundException() {

    }

    public BookingNotFoundException(String message) {
        super(message);
    }

    public BookingNotFoundException(Long id) {
        super("Бронирование с ID=" + id + " не найдено.");
    }

}
