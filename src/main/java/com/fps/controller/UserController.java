package com.fps.controller;

import com.fps.dto.CreateUserRequest;
import com.fps.dto.UserCreateResponse;
import com.fps.dto.UserStatsResponse;
import com.fps.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}/stats")
    public ResponseEntity<UserStatsResponse> getUserStats(@PathVariable String id) {
        return ResponseEntity.ok(userService.getUserStats(id));
    }

    @PostMapping
    public ResponseEntity<UserCreateResponse> createUser(
            @Valid @RequestBody CreateUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.createUser(request.username()));
    }
}
