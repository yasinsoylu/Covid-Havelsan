package com.havelsan.api.exception;

public class InvalidNewsFormatException extends RuntimeException {

  public InvalidNewsFormatException(String message) {
    super(message);
  }

  public InvalidNewsFormatException(String message, Throwable cause) {
    super(message, cause);
  }
}
