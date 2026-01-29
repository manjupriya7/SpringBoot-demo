package com.example.demo.api;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }
    @PostMapping
    public User createUser(@Valid @RequestBody User user){
        return userService.createUser(user);
    }
    @GetMapping("/{userId}")
    public User getUser(@PathVariable UUID userId){
        return userService.getUser(userId);
    }
    @PutMapping("/{userId}")
    public ResponseEntity<String> updateUser(@PathVariable UUID userId,@RequestBody User user){
        return ResponseEntity.ok(userService.updateUser(userId, user));
    }
    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable UUID userId){
        userService.deleteUser(userId);
        return ResponseEntity.ok("User deleted successfully");
    }
}
