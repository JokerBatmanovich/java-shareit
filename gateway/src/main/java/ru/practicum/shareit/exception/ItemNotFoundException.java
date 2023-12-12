package ru.practicum.shareit.exception;

public class ItemNotFoundException  extends RuntimeException {

    public ItemNotFoundException(Long id) {
        super("Вещь с ID=" + id + " не найдена.");
    }

}
