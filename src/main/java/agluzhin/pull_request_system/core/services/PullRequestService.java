package agluzhin.pull_request_system.core.services;

import agluzhin.pull_request_system.core.enums.ErrorCode;
import agluzhin.pull_request_system.core.enums.PullRequestStatus;
import agluzhin.pull_request_system.core.generators.ResponseGenerator;
import agluzhin.pull_request_system.core.models.*;

import agluzhin.pull_request_system.core.utils.DataValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;

@Service
public class PullRequestService {
    public ResponseEntity<?> create(PullRequest pullRequestToCreate) {
        if (InMemoryDataStorage.pullRequests.containsKey(pullRequestToCreate.pullRequestId())) {
            return ResponseGenerator.generateErrorResponse(
                    HttpStatus.BAD_REQUEST,
                    ErrorCode.PR_EXISTS,
                    "PR id already exists."
            );
        }
        User user = InMemoryDataStorage.users.get(pullRequestToCreate.authorId());
        if (DataValidator.isNull(user)) {
            return ResponseGenerator.generateErrorResponse(
                    HttpStatus.NOT_FOUND,
                    ErrorCode.NOT_FOUND,
                    "author not found."
            );
        }
        boolean isTeamExist = InMemoryDataStorage.teams.containsKey(user.teamName());
        boolean isTeamMemberExist = false;
        for (TeamMember member : InMemoryDataStorage.teams.get(user.teamName()).teamMembers()) {
            if (member.userId().equals(user.userId())) {
                isTeamMemberExist = true;
                break;
            }
        }
        if (!isTeamExist || !isTeamMemberExist) {
            return ResponseGenerator.generateErrorResponse(
                    HttpStatus.NOT_FOUND,
                    ErrorCode.NOT_FOUND,
                    "team/member not found."
            );
        }
        ArrayList<String> assignedReviewers = new ArrayList<>();
        ArrayList<TeamMember> members = InMemoryDataStorage.teams.get(user.teamName()).teamMembers();
        byte counter = 0;
        for (TeamMember member : members) {
            if (!member.userId().equals(user.userId()) && member.isActive()) {
                counter++;
                assignedReviewers.add(member.userId());
                if (counter == 2) {
                    break;
                }
            }
        }
        PullRequest newPullRequest = new PullRequest(
                pullRequestToCreate.pullRequestId(),
                pullRequestToCreate.pullRequestName(),
                pullRequestToCreate.authorId(),
                PullRequestStatus.OPEN,
                assignedReviewers,
                LocalDate.now(),
                null
        );
        InMemoryDataStorage.pullRequests.put(newPullRequest.pullRequestId(), newPullRequest);
        return ResponseGenerator.generateSuccessResponse(HttpStatus.CREATED, newPullRequest);
    }

    public ResponseEntity<?> merge(PullRequest pullRequestToMerge) {
        if (!InMemoryDataStorage.pullRequests.containsKey(pullRequestToMerge.pullRequestId())) {
            return ResponseGenerator.generateErrorResponse(
                    HttpStatus.NOT_FOUND,
                    ErrorCode.NOT_FOUND,
                    "PR not found."
            );
        }
        if (pullRequestToMerge.mergedAt() != null && pullRequestToMerge.mergedAt().equals(InMemoryDataStorage.pullRequests.get(pullRequestToMerge.pullRequestId()).mergedAt())) {
            return ResponseGenerator.generateSuccessResponse(HttpStatus.OK, pullRequestToMerge);
        }
        PullRequest pullRequest = InMemoryDataStorage.pullRequests.get(pullRequestToMerge.pullRequestId());
        PullRequest mergedPullRequest = new PullRequest(
                    pullRequest.pullRequestId(),
                    pullRequest.pullRequestName(),
                    pullRequest.authorId(),
                    PullRequestStatus.MERGED,
                    pullRequest.assignedReviewers(),
                    pullRequest.createdAt(),
                    LocalDate.now()
        );
        InMemoryDataStorage.pullRequests.put(mergedPullRequest.pullRequestId(), mergedPullRequest);
        return ResponseGenerator.generateSuccessResponse(HttpStatus.OK, mergedPullRequest);
    }

    public ResponseEntity<?> reassign(String pullRequestId, String oldUserId) {
        if (DataValidator.isNullOrEmptyString(pullRequestId, oldUserId)) {
            return ResponseGenerator.generateErrorResponse(
                    HttpStatus.BAD_REQUEST,
                    ErrorCode.BAD_REQUEST,
                    "fields 'pullRequestId' and 'oldUserId' shouldn't be empty."
            );
        }
        if (!InMemoryDataStorage.pullRequests.containsKey(pullRequestId) || !InMemoryDataStorage.users.containsKey(oldUserId)) {
            return ResponseGenerator.generateErrorResponse(
                    HttpStatus.NOT_FOUND,
                    ErrorCode.NOT_FOUND,
                    "PR or user not found."
            );
        }
        if (InMemoryDataStorage.pullRequests.get(pullRequestId).status() == PullRequestStatus.MERGED) {
            return ResponseGenerator.generateErrorResponse(
                    HttpStatus.CONFLICT,
                    ErrorCode.PR_MERGED,
                    "can't reassign on merged PR."
            );
        }
        if (!InMemoryDataStorage.pullRequests.get(pullRequestId).assignedReviewers().contains(oldUserId)) {
            return ResponseGenerator.generateErrorResponse(
                    HttpStatus.CONFLICT,
                    ErrorCode.NOT_ASSIGNED,
                    "reviewer is not assigned to this PR."
            );
        }
        User oldUser = InMemoryDataStorage.users.get(oldUserId);
        PullRequest pullRequest = InMemoryDataStorage.pullRequests.get(pullRequestId);
        ArrayList<String> assignedReviewers = pullRequest.assignedReviewers();
        ArrayList<TeamMember> members = InMemoryDataStorage.teams.get(oldUser.teamName()).teamMembers();
        for (String reviewer : assignedReviewers) {
            if (reviewer.equals(oldUserId)) {
                for (TeamMember member : members) {
                    if (!member.userId().equals(oldUserId) &&
                            member.isActive() == true &&
                            !member.userId().equals(pullRequest.authorId())) {
                        if (!assignedReviewers.contains(member.userId())) {
                            assignedReviewers.set(assignedReviewers.indexOf(oldUserId), member.userId());
                        } else {
                            if (assignedReviewers.size() > 1) {
                                assignedReviewers.remove(oldUserId);
                            }
                        }
                        PullRequest newPullRequest =  new PullRequest(
                                pullRequest.pullRequestId(),
                                pullRequest.pullRequestName(),
                                pullRequest.authorId(),
                                pullRequest.status(),
                                assignedReviewers,
                                pullRequest.createdAt(),
                                pullRequest.mergedAt()
                        );
                        InMemoryDataStorage.pullRequests.put(pullRequestId, newPullRequest);
                        Map<String, Object> body = Map.of(
                                "pullRequest", newPullRequest,
                                "replacedBy", member.userId()
                        );
                        return ResponseGenerator.generateSuccessResponse(HttpStatus.OK, body);
                    }
                }
            }
        }
        return ResponseGenerator.generateErrorResponse(
                HttpStatus.CONFLICT,
                ErrorCode.NO_CANDIDATE,
                "no active replacement candidate in team."
        );
    }
}
