package com.project.user.controller;

import com.project.user.dto.UserReq;
import com.project.user.dto.UserRes;
import com.project.user.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public  ResponseEntity<UserRes> register(@RequestBody @Valid UserReq req){
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.registerUser(req));
    }
    @GetMapping("/{id}")
    public ResponseEntity<UserRes> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserRes> getUserByEmail(@PathVariable String email) {
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }
}
