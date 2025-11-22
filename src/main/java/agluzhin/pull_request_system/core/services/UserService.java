package agluzhin.pull_request_system.core.services;

import agluzhin.pull_request_system.core.enums.ErrorCode;
import agluzhin.pull_request_system.core.generators.ResponseGenerator;
import agluzhin.pull_request_system.core.models.*;

import agluzhin.pull_request_system.core.utils.DataValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Map;

@Service
public class UserService {
    public ResponseEntity<?> setIsActive(User userToSetActive) {
        if (DataValidator.isNullOrEmptyString(userToSetActive.userId(), userToSetActive.userName(), userToSetActive.teamName())) {
            return ResponseGenerator.generateErrorResponse(
                    HttpStatus.BAD_REQUEST,
                    ErrorCode.BAD_REQUEST,
                    "fields 'userId', 'userName', 'teamName' shouldn't be empty."
            );
        }
        if (!InMemoryDataStorage.users.containsKey(userToSetActive.userId())) {
            return ResponseGenerator.generateErrorResponse(
                    HttpStatus.NOT_FOUND,
                    ErrorCode.NOT_FOUND,
                    "user not found."
            );
        }
        User changedUser = new User(
                userToSetActive.userId(),
                userToSetActive.userName(),
                userToSetActive.teamName(),
                true
        );
        InMemoryDataStorage.users.put(changedUser.userId(), changedUser);
        ArrayList<TeamMember> members = InMemoryDataStorage.teams.get(changedUser.teamName()).teamMembers();
        for (TeamMember member : members) {
            if (member.userId().equals(changedUser.userId())) {
                members.set(members.indexOf(member), new TeamMember(
                        changedUser.userId(),
                        changedUser.userName(),
                        changedUser.isActive()
                ));
                break;
            }
        }
        return ResponseGenerator.generateSuccessResponse(HttpStatus.OK, changedUser);
    }

    public ResponseEntity<?> getReview(String userId) {
        if (DataValidator.isNullOrEmptyString(userId)) {
            return ResponseGenerator.generateErrorResponse(
                    HttpStatus.BAD_REQUEST,
                    ErrorCode.BAD_REQUEST,
                    "field 'userId' shouldn't be empty."
            );
        }
        if (!InMemoryDataStorage.users.containsKey(userId)) {
            return ResponseGenerator.generateErrorResponse(
                    HttpStatus.NOT_FOUND,
                    ErrorCode.NOT_FOUND,
                    "user not found."
            );
        }
        return ResponseGenerator.generateSuccessResponse(HttpStatus.OK, Map.of("pullRequests", getShortPullRequestsReviewed(userId)));
    }

    private ArrayList<PullRequestShort> getShortPullRequestsReviewed(String userId) {
        ArrayList<PullRequestShort> shortPullRequests = new ArrayList<>();
        for (PullRequest pullRequest : InMemoryDataStorage.pullRequests.values()) {
            for (String reviewerId : pullRequest.assignedReviewers()) {
                if (reviewerId.equals(userId)) {
                    shortPullRequests.add(new PullRequestShort(
                            pullRequest.pullRequestId(),
                            pullRequest.pullRequestName(),
                            pullRequest.authorId(),
                            pullRequest.status()
                    ));
                }
            }
        }
        return shortPullRequests;
    }
}
