package ru.practicum.shareit.exception.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.booking.exeptions.BookingException;
import ru.practicum.shareit.exception.model.ErrorResponse;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.exception.model.ValidationException;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleValidationException(final ValidationException e) {
        log.warn(e.getMessage());
        return new ErrorResponse(
                String.format("Ошибка валидации:  \"%s\".", e.getMessage())
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleConflictException(final IllegalArgumentException e) {
        log.warn(e.getMessage());
        return new ErrorResponse(
                String.format("Ошибка валидации:  \"%s\".", e.getMessage())
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidExeption(MethodArgumentNotValidException ex) {
        List<String> exeptions = new ArrayList<>();
        ex.getAllErrors().forEach(err -> {
            log.warn(err.getDefaultMessage());
            exeptions.add(err.getDefaultMessage());
        });
        return new ErrorResponse(String.join(", ", exeptions));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(final NotFoundException e) {
        log.warn(e.getMessage());
        return new ErrorResponse(
                e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMissingException(final MissingRequestHeaderException e) {
        log.warn(e.getMessage());
        return new ErrorResponse(
                e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse bookingException(final BookingException e) {
        log.warn(e.getMessage());
        return new ErrorResponse(
                e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(final Throwable e) {
        System.out.println(e.getClass());
        log.error("Error: ", e);
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String stack = sw.toString();
        return new ErrorResponse(
                "Произошла непредвиденная ошибка.", stack
        );
    }
}
