package com.javatechie.controller;

import com.javatechie.dto.AuthRequestDTO;
import com.javatechie.dto.JwtResponseDTO;
import com.javatechie.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/library")
@RequiredArgsConstructor
public class LogInController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<JwtResponseDTO> authenticateAndGetToken(@RequestBody AuthRequestDTO authRequest) {
        JwtResponseDTO result = authenticationService.authenticateAndGetToken(authRequest);
        return ResponseEntity.ok(result);
    }
}