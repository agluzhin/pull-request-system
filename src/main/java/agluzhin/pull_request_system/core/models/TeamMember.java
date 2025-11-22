package agluzhin.pull_request_system.core.models;

public record TeamMember(
        String userId,
        String userName,
        boolean isActive
) {
}
