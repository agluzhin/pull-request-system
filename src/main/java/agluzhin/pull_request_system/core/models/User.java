package agluzhin.pull_request_system.core.models;

public record User(
        String userId,
        String userName,
        String teamName,
        boolean isActive
) {
}
