package agluzhin.pull_request_system.core.models;

import agluzhin.pull_request_system.core.enums.PullRequestStatus;

import java.time.LocalDate;
import java.util.ArrayList;

public record PullRequest(
        String pullRequestId,
        String pullRequestName,
        String authorId,
        PullRequestStatus status,
        ArrayList<String> assignedReviewers,
        LocalDate createdAt,
        LocalDate mergedAt
) {
}
