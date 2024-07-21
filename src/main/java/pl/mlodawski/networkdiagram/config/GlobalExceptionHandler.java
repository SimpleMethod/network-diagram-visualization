package pl.mlodawski.networkdiagram.config;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler  extends ResponseEntityExceptionHandler {

    /**
     * Handles the exception thrown when an invalid link is encountered.
     *
     * @param ex      The IllegalArgumentException that was thrown.
     * @param request The current WebRequest.
     * @return A ResponseEntity containing the error response.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidLinkException(IllegalArgumentException ex, WebRequest request) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", HttpStatus.BAD_REQUEST.getReasonPhrase());
        response.put("message", ex.getMessage());
        response.put("path", request.getDescription(false).substring(4));

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");

        return new ResponseEntity<>(response, headers, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles the exception thrown when the HTTP message cannot be read.
     *
     * @param ex      The HttpMessageNotReadableException that was thrown.
     * @param headers The HttpHeaders of the HTTP request.
     * @param status  The HttpStatusCode of the HTTP response.
     * @param request The current WebRequest.
     * @return A ResponseEntity containing the error response.
     */
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Throwable cause = ex.getCause();
        if (cause instanceof InvalidFormatException ife) {
            String fieldName = ife.getPath().getFirst().getFieldName();
            String invalidValue = ife.getValue().toString();
            String errorMessage = String.format("Invalid value '%s' for enum '%s'", invalidValue, fieldName);

            Map<String, Object> response = new HashMap<>();
            response.put("timestamp", LocalDateTime.now());
            response.put("status", HttpStatus.BAD_REQUEST.value());
            response.put("error", HttpStatus.BAD_REQUEST.getReasonPhrase());
            response.put("message", errorMessage);
            response.put("path", request.getDescription(false).substring(4));

            headers.add(HttpHeaders.CONTENT_TYPE, "application/json");

            return new ResponseEntity<>(response, headers, HttpStatus.BAD_REQUEST);
        }

        return super.handleHttpMessageNotReadable(ex, headers, status, request);
    }
}
