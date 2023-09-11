package com.javatechie.controller;

import com.javatechie.dto.JwtResponseDTO;
import com.javatechie.dto.RefreshTokenRequestDTO;
import com.javatechie.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/library")
public class RefreshTokenController {

    private final RefreshTokenService refreshTokenService;

    @PostMapping("/refreshToken")
    public ResponseEntity<JwtResponseDTO> refreshToken(@RequestBody RefreshTokenRequestDTO refreshTokenRequest) {
        JwtResponseDTO result = refreshTokenService.refreshToken(refreshTokenRequest);
        return ResponseEntity.ok(result);
    }
}
