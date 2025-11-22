package agluzhin.pull_request_system.core.services;

import agluzhin.pull_request_system.core.enums.ErrorCode;
import agluzhin.pull_request_system.core.generators.ResponseGenerator;
import agluzhin.pull_request_system.core.models.*;

import agluzhin.pull_request_system.core.utils.DataValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class TeamService {
    public ResponseEntity<?> getTeam(String teamName) {
        if (DataValidator.isNullOrEmptyString(teamName)) {
            return ResponseGenerator.generateErrorResponse(
                    HttpStatus.BAD_REQUEST,
                    ErrorCode.BAD_REQUEST,
                    "field 'teamName' shouldn't be empty."
            );
        }
        Team team = InMemoryDataStorage.teams.get(teamName);
        if (DataValidator.isNull(team)) {
            return ResponseGenerator.generateErrorResponse(
                    HttpStatus.NOT_FOUND,
                    ErrorCode.NOT_FOUND,
                    "team not found."
            );
        }
        return ResponseGenerator.generateSuccessResponse(HttpStatus.OK, team);
    }

    public ResponseEntity<?> addTeam(Team teamToAdd) {
        if (InMemoryDataStorage.teams.containsKey(teamToAdd.teamName())) {
            return ResponseGenerator.generateErrorResponse(
                    HttpStatus.BAD_REQUEST,
                    ErrorCode.TEAM_EXISTS,
                    "team already exists."
            );
        }
        if (DataValidator.isNullOrEmptyString(teamToAdd.teamName()) ||
                DataValidator.isNullOrEmptyArrayList(teamToAdd.teamMembers())) {
            return ResponseGenerator.generateErrorResponse(
                    HttpStatus.BAD_REQUEST,
                    ErrorCode.BAD_REQUEST,
                    "fields 'teamName' and 'teamMembers' shouldn't be empty."
            );
        }
        for (TeamMember member : teamToAdd.teamMembers()) {
            if (DataValidator.isNullOrEmptyString(member.userId(), member.userName())) {
                return ResponseGenerator.generateErrorResponse(
                        HttpStatus.BAD_REQUEST,
                        ErrorCode.BAD_REQUEST,
                        "fields 'userId' and 'userName' shouldn't be empty."
                );
            }
            User user = new User(
                    member.userId(),
                    member.userName(),
                    teamToAdd.teamName(),
                    member.isActive()
            );
            InMemoryDataStorage.users.put(user.userId(), user);
        }
        InMemoryDataStorage.teams.put(teamToAdd.teamName(), teamToAdd);
        return ResponseGenerator.generateSuccessResponse(HttpStatus.CREATED, teamToAdd);
    }
}
