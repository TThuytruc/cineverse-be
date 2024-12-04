package com.hcmus.cineverse_be.controller;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.hcmus.cineverse_be.request.RegisterRequest;
import com.hcmus.cineverse_be.response.BasicResponse;
import com.hcmus.cineverse_be.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<Object> register(@RequestBody RegisterRequest userRequest) {
        userService.create(userRequest.getUsername(), userRequest.getEmail(), userRequest.getPassword());
        BasicResponse response = new BasicResponse("User registered successfully.");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
   }

    @GetMapping("/verify")
    public ResponseEntity<Map<String, Boolean>> verifyToken(@RequestHeader("Authorization") String authorizationHeader) {
        Map<String, Boolean> response = new HashMap<>();
        try {
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                response.put("authenticated", false);
                return ResponseEntity.ok(response);
            }

            String token = authorizationHeader.substring(7);
            FirebaseAuth.getInstance().verifyIdToken(token);
            response.put("authenticated", true);
        } catch (FirebaseAuthException e) {
            response.put("authenticated", false);
        }

        return ResponseEntity.ok(response);
    }
}
