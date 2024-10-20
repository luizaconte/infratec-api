package br.com.infratec.exception;

import org.springframework.http.HttpStatus;

public class InfratecException extends RuntimeException {

    private HttpStatus status;

    public InfratecException() {
    }

    public InfratecException(String message) {
        super(message);
    }

    public InfratecException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public InfratecException(Throwable cause) {
        super(cause);
    }

    @Override
    public String toString() {
        return "InfatrecException: " + getMessage();
    }

    public HttpStatus getStatus() {
        return status;
    }
}
