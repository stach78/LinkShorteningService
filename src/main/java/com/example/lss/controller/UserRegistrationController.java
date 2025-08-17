package com.example.lss.controller;

import com.example.lss.dto.UserRegisterRequest;
import com.example.lss.dto.UserRegisterResponse;
import com.example.lss.entity.UserAccount;
import com.example.lss.service.UserAccountService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserRegistrationController {

    private final UserAccountService service;

    public UserRegistrationController(UserAccountService service) {
        this.service = service;
    }

    @PostMapping("/register")
    public ResponseEntity<UserRegisterResponse> register(@RequestBody @Valid UserRegisterRequest req) {
        UserAccount u = service.registerUser(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(new UserRegisterResponse(u.getUsername()));
    }

    @DeleteMapping("/delete_user")
    public ResponseEntity<Void> deleteOwnAccount(
            @org.springframework.security.core.annotation.AuthenticationPrincipal(expression = "username") String username) {
        if (username == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        service.deleteUserAndLinks(username);
        return ResponseEntity.noContent().build();
    }
}
