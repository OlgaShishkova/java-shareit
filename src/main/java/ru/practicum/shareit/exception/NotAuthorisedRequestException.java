package ru.practicum.shareit.exception;

public class NotAuthorisedRequestException extends RuntimeException {
    public NotAuthorisedRequestException(String message) {
        super(message);
    }
}
