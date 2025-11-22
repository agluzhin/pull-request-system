package agluzhin.pull_request_system.core.handlers;

import agluzhin.pull_request_system.core.enums.ErrorCode;
import agluzhin.pull_request_system.core.models.Error;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleInvalidJson(Exception ex) {
        Map<String, Object> body = Map.of(
                "error", new Error(
                        ErrorCode.BAD_REQUEST,
                        "incorrect data given."
                )
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }
}
