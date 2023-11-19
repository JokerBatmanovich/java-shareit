package ru.practicum.shareit.exception;

public class ItemRequestNotFoundException extends RuntimeException {
    public ItemRequestNotFoundException() {

    }

    public ItemRequestNotFoundException(String message) {
        super(message);
    }

    public ItemRequestNotFoundException(Long id) {
        super("Запрос с ID=" + id + " не найден");
    }

}
