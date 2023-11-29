package ru.practicum.shareit.exception;

public class CommentNotFoundException extends RuntimeException {
    public CommentNotFoundException() {
    }

    public CommentNotFoundException(String message) {
        super(message);
    }

    public CommentNotFoundException(Long id) {
        super("Отызв с ID=" + id + " не найден.");
    }

}
