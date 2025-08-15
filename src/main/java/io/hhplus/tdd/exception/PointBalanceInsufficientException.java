package io.hhplus.tdd.exception;

import org.springframework.http.HttpStatus;

public class PointBalanceInsufficientException extends CustomException{

    private static final String ERROR_CODE = "POINT_BALANCE_INSUFFICIENT";
    private static final String DEFAULT_MESSAGE = "사용할 포인트 잔액이 부족합니다.";

    public PointBalanceInsufficientException() {
        super(HttpStatus.BAD_REQUEST, ERROR_CODE, DEFAULT_MESSAGE);
    }

    public PointBalanceInsufficientException(String customMessage) {
        super(HttpStatus.BAD_REQUEST, ERROR_CODE, customMessage);
    }

}
