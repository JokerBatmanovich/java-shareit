package ru.practicum.shareit.exception;

public class NoPermissionException extends RuntimeException {
    public NoPermissionException() {
    }

    public NoPermissionException(String message) {
        super(message);
    }

    public NoPermissionException(Long id) {
        super("Пользователь с ID=" + id + " не имеет прав на данное действие.");
    }

}
