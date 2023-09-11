package com.javatechie.service;

import com.javatechie.dto.AuthRequestDTO;
import com.javatechie.dto.JwtResponseDTO;

public interface AuthenticationService {
    JwtResponseDTO authenticateAndGetToken(AuthRequestDTO authRequest);

}
