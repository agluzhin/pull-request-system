package agluzhin.pull_request_system.core.models;

import agluzhin.pull_request_system.core.enums.ErrorCode;

public record Error(
        ErrorCode code,
        String message
) {
}
