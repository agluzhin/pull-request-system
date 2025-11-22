package agluzhin.pull_request_system.core.generators;

import agluzhin.pull_request_system.core.enums.ErrorCode;
import agluzhin.pull_request_system.core.models.Error;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public class ResponseGenerator {
    public static ResponseEntity<?> generateErrorResponse(HttpStatus httpStatus, ErrorCode errorCode, String message) {
        Map<String, Object> body = Map.of(
                "error", new Error(
                        errorCode,
                        message
                )
        );
        return ResponseEntity.status(httpStatus).body(body);
    }

    public static ResponseEntity<?> generateSuccessResponse(HttpStatus httpStatus, Object body) {
        return ResponseEntity.status(httpStatus).body(body);
    }
}
