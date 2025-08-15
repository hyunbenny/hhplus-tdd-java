package io.hhplus.tdd.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomException extends RuntimeException{

    private final HttpStatus HttpStatus;
    private final String errorCode;

    protected CustomException(HttpStatus httpStatus, String errorCode, String message) {
        super(message);
        HttpStatus = httpStatus;
        this.errorCode = errorCode;
    }

}
