package agluzhin.pull_request_system.core.controllers;

import agluzhin.pull_request_system.core.models.PullRequest;
import agluzhin.pull_request_system.core.services.PullRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pullRequest")
public class PullRequestController {
    private final PullRequestService pullRequestService;

    public PullRequestController(PullRequestService pullRequestService) {
        this.pullRequestService = pullRequestService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(
            @RequestBody PullRequest pullRequestToCreate
    ) {
        return pullRequestService.create(pullRequestToCreate);
    }

    @PostMapping("/merge")
    public ResponseEntity<?> merge(
            @RequestBody PullRequest pullRequestToMerge
    ) {
        return pullRequestService.merge(pullRequestToMerge);
    }

    @PostMapping("/reassign")
    public ResponseEntity<?> reassign(
            @RequestParam("pullRequestId") String pullRequestId,
            @RequestParam("oldUserId") String oldUserId
    ) {
        return pullRequestService.reassign(pullRequestId, oldUserId);
    }
}
