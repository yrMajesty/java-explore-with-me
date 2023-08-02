package ru.practicum.mainservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.mainservice.utils.DateTimeUtils;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ErrorResponse handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        Map<String, Object> response = new LinkedHashMap<>();

        Map<String, Object> errors = new LinkedHashMap<>();
        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        response.put("errors", errors);

        log.error("Error MethodArgumentNotValidException {}", ex.getMessage());

        return ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.name())
                .errors(errors)
                .reason("Required parameters is not valid in the request")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now().format(DateTimeUtils.DATE_TIME_FORMATTER))
                .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    protected ErrorResponse handleMissingServletRequestParameter(MissingServletRequestParameterException ex) {
        log.error("Error MissingServletRequestParameterException {}", ex.getMessage());

        return ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.name())
                .reason("Required parameter is not specified in the request")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now().format(DateTimeUtils.DATE_TIME_FORMATTER))
                .build();
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoFoundObjectException.class)
    public ErrorResponse handleNoFoundObjectException(NoFoundObjectException e) {
        log.error("Error NoFoundObjectException {}", e.getMessage());

        return ErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND.name())
                .reason("The object was not found.")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now().format(DateTimeUtils.DATE_TIME_FORMATTER))
                .build();
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = Exception.class)
    public ErrorResponse handleException(Exception e) {
        log.error("Error Exception {}", e.getMessage());

        e.printStackTrace();

        return ErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.name())
                .reason("Unexpected error")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now().format(DateTimeUtils.DATE_TIME_FORMATTER))
                .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = InvalidRequestException.class)
    public ErrorResponse handlerInvalidRequestException(InvalidRequestException e) {
        log.error("Error InvalidRequestException {}", e.getMessage());

        return ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.name())
                .reason("Request is incorrect.")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now().format(DateTimeUtils.DATE_TIME_FORMATTER))
                .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = DateTimeValidationException.class)
    public ErrorResponse handlerDateTimeValidationException(DateTimeValidationException e) {
        log.error("Error DateTimeValidationException {}", e.getMessage());

        return ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.name())
                .reason("Date time of request is not valid")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now().format(DateTimeUtils.DATE_TIME_FORMATTER))
                .build();
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(value = EventParametersException.class)
    public ErrorResponse handlerEventParametersException(EventParametersException e) {
        log.error("Error EventParametersException {}", e.getMessage());

        return ErrorResponse.builder()
                .status(HttpStatus.CONFLICT.name())
                .reason("Discrepancy between the parameters of the \"Event\" object")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now().format(DateTimeUtils.DATE_TIME_FORMATTER))
                .build();

    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ErrorResponse handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        log.error("Error DataIntegrityViolationException {}", e.getMessage());

        return ErrorResponse.builder()
                .status(HttpStatus.CONFLICT.name())
                .reason("Error of saving to the database")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now().format(DateTimeUtils.DATE_TIME_FORMATTER))
                .build();
    }
}