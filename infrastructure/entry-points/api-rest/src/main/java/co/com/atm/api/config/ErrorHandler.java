package co.com.atm.api.config;

import co.com.atm.api.dto.Response;
import co.com.atm.usecase.exceptions.AccountNotFoundException;
import co.com.atm.usecase.exceptions.InsufficientBalanceException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.ConnectException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ControllerAdvice
public class ErrorHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {InsufficientBalanceException.class})
    public ResponseEntity<Response> custom(InsufficientBalanceException ex){
        Response response = Response.builder()
                .fecha(LocalDateTime.now().toString())
                .codigoResultado("EXPECTATION_FAILED")
                .descripcionRespuesta(ex.getMessage())
                .result(Collections.emptyList())
                .build();

        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(response);
    }
    @ExceptionHandler(value = {AccountNotFoundException.class})
    public ResponseEntity<Response> custom(AccountNotFoundException ex){
        Response response = Response.builder()
                .fecha(LocalDateTime.now().toString())
                .codigoResultado("NO_FOUND")
                .descripcionRespuesta(ex.getMessage())
                .result(Collections.emptyList())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
    @ExceptionHandler(value = {RuntimeException.class})
    public ResponseEntity<Response> custom(RuntimeException ex){

        Response response = Response.builder()
                .fecha(LocalDateTime.now().toString())
                .codigoResultado("BAD_REQUEST")
                .descripcionRespuesta(ex.getMessage())
                .result(Collections.emptyList())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(value = {ConnectException.class})
    public ResponseEntity<Response> connectionFail(ConnectException ex){
        Response response = Response.builder()
                .fecha(LocalDateTime.now().toString())
                .codigoResultado("SERVICE_UNAVAILABLE")
                .descripcionRespuesta(ex.getMessage())
                .result(Collections.emptyList())
                .build();
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        Response response = Response.builder()
                .fecha(LocalDateTime.now().toString())
                .codigoResultado("UNPROCESSABLE_ENTITY")
                .descripcionRespuesta(ex.getMessage())
                .result(Collections.emptyList())
                .build();

        return new ResponseEntity<>(response, HttpStatus.UNPROCESSABLE_ENTITY);
    }
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> constraintViolationException(ConstraintViolationException ex, WebRequest request) {
        List<String> errors = new ArrayList<>();

        ex.getConstraintViolations().forEach(cv -> errors.add(cv.getMessage()));

        Response response = Response.builder()
                .fecha(LocalDateTime.now().toString())
                .codigoResultado("BAD_REQUEST")
                .descripcionRespuesta(errors.stream().reduce(" ", String::concat))
                .result(Collections.emptyList())
                .build();

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
