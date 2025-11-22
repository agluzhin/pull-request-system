package agluzhin.pull_request_system.core.models;

import agluzhin.pull_request_system.core.enums.PullRequestStatus;

public record PullRequestShort(
        String pullRequestId,
        String pullRequestName,
        String authorId,
        PullRequestStatus status
) {
}
