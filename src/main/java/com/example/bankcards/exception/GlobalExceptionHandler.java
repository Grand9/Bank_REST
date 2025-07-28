package com.example.bankcards.exception;

import com.example.bankcards.dto.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        return e.getBindingResult().getAllErrors().stream()
                .collect(Collectors.toMap(
                        error -> ((FieldError) error).getField(),
                        error -> Objects.requireNonNullElse(error.getDefaultMessage(), "")
                ));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.REQUEST_ENTITY_TOO_LARGE)
    public ErrorResponse handleMaxUploadSizeExceededException(RuntimeException e, HttpServletRequest request) {
        log.error("Max upload size exceeded exception: {}", e.getMessage(), e);
        return buildErrorResponse(e, request);
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        log.error("Runtime exception: {}", e.getMessage(), e);
        return buildErrorResponse(e, request);
    }

    @ExceptionHandler(CardNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleCardNotFoundException(CardNotFoundException e, HttpServletRequest request) {
        log.error("Card not found: {}", e.getMessage(), e);
        return buildErrorResponse(e, request);
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFoundException(UserNotFoundException e, HttpServletRequest request) {
        log.error("User not found: {}", e.getMessage(), e);
        return buildErrorResponse(e, request);
    }

    @ExceptionHandler(InvalidTokenException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleInvalidTokenException(InvalidTokenException e, HttpServletRequest request) {
        log.error("Invalid token: {}", e.getMessage(), e);
        return buildErrorResponse(e, request);
    }

    @ExceptionHandler(TransferException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleTransferException(TransferException e, HttpServletRequest request) {
        log.error("Transfer exception: {}", e.getMessage(), e);
        return buildErrorResponse(e, request);
    }

    private ErrorResponse buildErrorResponse(Exception e, HttpServletRequest request) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .url(request.getRequestURI())
                .message(e.getMessage())
                .build();
    }
}