package agluzhin.pull_request_system.core.controllers;

import agluzhin.pull_request_system.core.models.User;
import agluzhin.pull_request_system.core.services.UserService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/setIsActive")
    public ResponseEntity<?> setIsActive(
            @RequestBody User userToSetActive
    ) {
        return userService.setIsActive(userToSetActive);
    }

    @GetMapping("/getReview")
    public ResponseEntity<?> getReview(
            @RequestParam("userId") String userId
    ) {
        return userService.getReview(userId);
    }

}
