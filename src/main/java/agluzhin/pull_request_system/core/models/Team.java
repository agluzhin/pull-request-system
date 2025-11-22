package agluzhin.pull_request_system.core.models;

import java.util.ArrayList;

public record Team(
        String teamName,
        ArrayList<TeamMember> teamMembers
) {
}
