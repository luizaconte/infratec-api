package br.com.infratec.exception;


import br.com.infratec.dto.ErrorResponseDTO;
import br.com.infratec.dto.FieldErrorDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class ApiErrorHandler extends ResponseEntityExceptionHandler {


    @ResponseStatus(value = HttpStatus.PRECONDITION_FAILED)
    @ResponseBody
    @ExceptionHandler(PossibleSqlInjectionAttackException.class)
    public ResponseEntity<?> possibleSqlInjectionAttackExceptionHandler(
            final PossibleSqlInjectionAttackException exception) {
        log.error("Exception Caught:", exception);
        final var response = new HashMap<String, String>();
        response.put("Message", "Trying to attack me with SQL Injection?, Fuck you!");
        response.put("exeption", exception.getClass().getSimpleName());
        response.put("timestamp",
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSS").format(LocalDateTime.now(ZoneId.of("+00:00"))));
        return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body(response);
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<ErrorResponseDTO> handleUnauthorizedException(Exception e) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        return new ResponseEntity<>(
                ErrorResponseDTO.builder()
                        .status(status.name())
                        .code(status.value())
                        .message(e.getMessage())
                        .build()
                ,
                status
        );
    }


    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {

        List<FieldErrorDTO> validationList = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(fieldError -> FieldErrorDTO.builder()
                        .field(fieldError.getField())
                        .message(fieldError.getDefaultMessage())
                        .build())
                .collect(Collectors.toList());

        return new ResponseEntity<>(
                ErrorResponseDTO.builder()
                        .status(status.toString())
                        .code(status.value())
                        .message("Erro de validação")
                        .timestamp(new Date())
                        .fieldErrors(validationList)
                        .build()
                ,
                status
        );
    }

    @ExceptionHandler(InfratecException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponseDTO> handleInfratecException(InfratecException e) {
        HttpStatus status = Objects.isNull(e.getStatus()) ? HttpStatus.BAD_REQUEST : e.getStatus();
        return new ResponseEntity<>(
                ErrorResponseDTO.builder()
                        .status(status.name())
                        .code(status.value())
                        .message(e.getMessage())
                        .timestamp(new Date())
                        .build()
                ,
                status
        );
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponseDTO> handleNotFoundException(NotFoundException e) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        return new ResponseEntity<>(
                ErrorResponseDTO.builder()
                        .status(status.name())
                        .code(status.value())
                        .message("Recurso não encontrado")
                        .timestamp(new Date())
                        .build()
                ,
                status
        );
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class) // exception handled
    public ResponseEntity<ErrorResponseDTO> handleExceptions(Exception e) {

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        e.printStackTrace(printWriter);
        String stackTrace = stringWriter.toString();

        return new ResponseEntity<>(
                ErrorResponseDTO.builder()
                        .status(HttpStatus.INTERNAL_SERVER_ERROR.name())
                        .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .message(e.getMessage())
                        .stackTrace(stackTrace)
                        .build()
                ,
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
