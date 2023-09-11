package com.javatechie.service;

import com.javatechie.dto.JwtResponseDTO;
import com.javatechie.dto.RefreshTokenRequestDTO;

public interface RefreshTokenService {
    JwtResponseDTO refreshToken(RefreshTokenRequestDTO refreshTokenRequest);
}



