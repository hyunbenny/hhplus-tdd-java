package io.hhplus.tdd.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCodes {
    POINT_BALANCE_INSUFFICIENT("POINT_BALANCE_INSUFFICIENT", "사용 가능한 포인트가 부족합니다.", HttpStatus.BAD_REQUEST),
    INVALID_TRANSACTION_TYPE("INVALID_TRANSACTION_TYPE", "올바르지 않은 트랜잭션 타입입니다..", HttpStatus.BAD_REQUEST),
    POINT_AMOUNT_INVALID("POINT_AMOUNT_INVALID", "포인트 금액은 0보다 커야 합니다.", HttpStatus.BAD_REQUEST),
    USER_NOT_EXIST("USER_NOT_EXIST", "사용자 정보가 존재하지 않습니다.", HttpStatus.NOT_FOUND);

    private final String code;
    private final String defaultMessage;
    private final HttpStatus httpStatus;

    ErrorCodes(String code, String defaultMessage, HttpStatus httpStatus) {
        this.code = code;
        this.defaultMessage = defaultMessage;
        this.httpStatus = httpStatus;
    }

    public String getCode() {
        return code;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

}
