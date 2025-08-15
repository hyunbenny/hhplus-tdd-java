package io.hhplus.tdd.exception;

import org.springframework.http.HttpStatus;

public class PointAmountInvalidException extends CustomException{

    private static final String ERROR_CODE = "POINT_AMOUNT_INVALID";
    private static final String DEFAULT_MESSAGE = "포인트 금액은 0보다 커야 합니다.";

    public PointAmountInvalidException() {
        super(HttpStatus.BAD_REQUEST, ERROR_CODE, DEFAULT_MESSAGE);
    }

    public PointAmountInvalidException(String customMessage) {
        super(HttpStatus.BAD_REQUEST, ERROR_CODE, customMessage);
    }

}
