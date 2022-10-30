package ru.practicum.shareit.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserAlreadyExistException;
import ru.practicum.shareit.exception.UserNotFoundException;

import javax.validation.ConstraintViolationException;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler({UserNotFoundException.class, ItemNotFoundException.class})
    public ResponseEntity<String> handleNotFoundException(RuntimeException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class})
    public ResponseEntity<String> handleValidationException(RuntimeException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<String> handleAlreadyExistException(UserAlreadyExistException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    public ResponseEntity<String> handleException(Throwable e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>("Произошла непредвиденная ошибка", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
