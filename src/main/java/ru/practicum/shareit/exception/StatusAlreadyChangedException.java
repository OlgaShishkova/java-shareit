package ru.practicum.shareit.exception;

public class StatusAlreadyChangedException extends RuntimeException {
    public StatusAlreadyChangedException(String message) {
        super(message);
    }
}
