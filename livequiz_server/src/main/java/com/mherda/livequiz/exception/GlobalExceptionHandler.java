package com.mherda.livequiz.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(NoOpenSessionException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleNoOpenSessionException(
            NoOpenSessionException noOpenSessionException) {
      return ResponseEntity.badRequest().body(noOpenSessionException.getMessage());
    }

    @ExceptionHandler(NoSuchAnswerException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleNoOpenSessionException(
            NoSuchAnswerException noSuchAnswerException) {
      return ResponseEntity.badRequest().body(noSuchAnswerException.getMessage());
    }

}