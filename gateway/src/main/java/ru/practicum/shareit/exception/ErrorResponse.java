package ru.practicum.shareit.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ErrorResponse {
    final String error;

    public ErrorResponse(String message) {
        this.error = message;
    }
}
