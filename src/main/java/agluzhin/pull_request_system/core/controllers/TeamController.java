package agluzhin.pull_request_system.core.controllers;

import agluzhin.pull_request_system.core.models.Team;
import agluzhin.pull_request_system.core.services.TeamService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/team")
public class TeamController {
    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @GetMapping("/get")
    public ResponseEntity<?> getTeam(
            @RequestParam("teamName") String teamName
    ) {
        return teamService.getTeam(teamName);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addTeam(
            @RequestBody Team teamToAdd
    ) {
        return teamService.addTeam(teamToAdd);
    }
}
