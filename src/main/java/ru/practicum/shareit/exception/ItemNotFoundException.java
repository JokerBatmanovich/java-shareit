package ru.practicum.shareit.exception;

public class ItemNotFoundException  extends RuntimeException {
    public ItemNotFoundException() {

    }

    public ItemNotFoundException(String message) {
        super(message);
    }

    public ItemNotFoundException(Long id) {
        super("Вещь с ID=" + id + " не найдена.");
    }

}
