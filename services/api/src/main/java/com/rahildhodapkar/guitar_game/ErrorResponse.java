package com.rahildhodapkar.guitar_game;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.http.HttpStatus;

public record ErrorResponse(
    LocalDateTime timestamp,
    int status,
    String error,
    String message,
    String path,
    List<String> details) {
  public static ErrorResponse of(HttpStatus status, String message, String path) {
    return new ErrorResponse(
        LocalDateTime.now(), status.value(), status.name(), message, path, List.of());
  }

  public static ErrorResponse of(
      HttpStatus status, String message, String path, List<String> details) {
    return new ErrorResponse(
        LocalDateTime.now(), status.value(), status.name(), message, path, details);
  }
}
