package io.hhplus.tdd.exception;

public class UserNotExistException extends RuntimeException{
    private final String errorCode;

    public UserNotExistException() {
        this("USER_NOT_EXIST", "User does not exist.");
    }

    public UserNotExistException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
