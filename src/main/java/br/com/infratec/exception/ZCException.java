package br.com.infratec.exception;

import org.springframework.http.HttpStatus;

public class ZCException extends RuntimeException {

    private HttpStatus status;

    public ZCException() {
    }

    public ZCException(String message) {
        super(message);
    }

    public ZCException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public ZCException(Throwable cause) {
        super(cause);
    }

    @Override
    public String toString() {
        return "FiorilliException: " + getMessage();
    }

    public HttpStatus getStatus() {
        return status;
    }
}
