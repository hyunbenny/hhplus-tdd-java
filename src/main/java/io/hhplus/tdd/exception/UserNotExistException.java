package io.hhplus.tdd.exception;

import org.springframework.http.HttpStatus;

public class UserNotExistException extends CustomException{

    private static final String ERROR_CODE = "USER_POINT_NOT_EXIST";
    private static final String DEFAULT_MESSAGE = "데이터가 존재하지 않습니다.";

    public UserNotExistException() {
        super(HttpStatus.NOT_FOUND, ERROR_CODE, DEFAULT_MESSAGE);
    }

    public UserNotExistException(String customMessage) {
        super(HttpStatus.NOT_FOUND, ERROR_CODE, customMessage);
    }
}
