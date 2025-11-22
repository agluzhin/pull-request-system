package agluzhin.pull_request_system.core.models;

import java.util.Map;
import java.util.HashMap;

public class InMemoryDataStorage {
    public static final Map<String, Team> teams = new HashMap<>();
    public static final Map<String, User> users = new HashMap<>();
    public static final Map<String, PullRequest> pullRequests = new HashMap<>();
}
