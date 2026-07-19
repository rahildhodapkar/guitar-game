package com.rahildhodapkar.guitar_game;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<ErrorResponse> handleMissingParam(
      MissingServletRequestParameterException ex, WebRequest request) {
    ErrorResponse body =
        ErrorResponse.of(
            HttpStatus.BAD_REQUEST,
            "Required request parameter '" + ex.getParameterName() + "' is missing",
            request.getDescription(false));
    return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ErrorResponse> handleTypeMismatch(
      MethodArgumentTypeMismatchException ex, WebRequest request) {
    ErrorResponse body =
        ErrorResponse.of(
            HttpStatus.BAD_REQUEST,
            "Parameter '" + ex.getName() + "' has an invalid value",
            request.getDescription(false));
    return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgument(
      IllegalArgumentException ex, WebRequest request) {
    ErrorResponse body =
        ErrorResponse.of(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getDescription(false));
    return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationErrors(
      MethodArgumentNotValidException ex, WebRequest request) {
    List<String> details =
        ex.getBindingResult().getFieldErrors().stream()
            .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
            .toList();
    ErrorResponse body =
        ErrorResponse.of(
            HttpStatus.BAD_REQUEST, "Validation failed", request.getDescription(false), details);
    return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ErrorResponse> handleConstraintViolation(
      ConstraintViolationException ex, WebRequest request) {
    List<String> details =
        ex.getConstraintViolations().stream().map(ConstraintViolation::getMessage).toList();
    ErrorResponse body =
        ErrorResponse.of(
            HttpStatus.BAD_REQUEST, "Constraint violation", request.getDescription(false), details);
    return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorResponse> handleUnreadableMessage(
      HttpMessageNotReadableException ex, WebRequest request) {
    ErrorResponse body =
        ErrorResponse.of(
            HttpStatus.BAD_REQUEST, "Malformed request body", request.getDescription(false));
    return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(NoHandlerFoundException.class)
  public ResponseEntity<ErrorResponse> handleNoHandlerFound(
      NoHandlerFoundException ex, WebRequest request) {
    ErrorResponse body =
        ErrorResponse.of(
            HttpStatus.NOT_FOUND,
            "No endpoint found for " + ex.getHttpMethod() + " " + ex.getRequestURL(),
            request.getDescription(false));
    return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleUnhandled(Exception ex, WebRequest request) {
    ErrorResponse body =
        ErrorResponse.of(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "An unexpected error occurred.",
            request.getDescription(false));
    return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
