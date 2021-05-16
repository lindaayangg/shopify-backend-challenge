package com.example.shopifychallenge.exceptions;

import com.example.shopifychallenge.dtos.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Collections;

@SuppressWarnings({"unchecked", "rawtypes"})
@ControllerAdvice
@Slf4j
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(Exception.class)
    public final ResponseEntity<Object> handleAllExceptions(Exception ex) {
        ErrorResponse error = new ErrorResponse("Unhandled Error", Collections.singletonList(ex.getLocalizedMessage()));
        return new ResponseEntity(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(InternalErrorException.class)
    public final ResponseEntity<Object> handleAllExceptions(InternalErrorException ex) {
        ErrorResponse error = new ErrorResponse("Internal Error", Collections.singletonList(ex.getLocalizedMessage()));
        return new ResponseEntity(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(RecordNotFoundException.class)
    public final ResponseEntity<Object> handleRecordNotFoundException(RecordNotFoundException ex) {
        ErrorResponse error = new ErrorResponse("Record Not Found", Collections.singletonList(ex.getLocalizedMessage()));
        return new ResponseEntity(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ForbiddenException.class)
    public final ResponseEntity<Object> handleRecordNotFoundException(ForbiddenException ex) {
        ErrorResponse error = new ErrorResponse("Forbidden", Collections.singletonList(ex.getLocalizedMessage()));
        return new ResponseEntity(error, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(BadRequestException.class)
    public final ResponseEntity<Object> handleBadRequestException(BadRequestException ex) {
        ErrorResponse error = new ErrorResponse("Bad Request", Collections.singletonList(ex.getLocalizedMessage()));
        return new ResponseEntity(error, HttpStatus.BAD_REQUEST);
    }
}