package com.javatechie.service.Impl;

import com.javatechie.entity.RefreshToken;
import com.javatechie.repository.RefreshTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LogOutServiceImpl {

    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public String logout(String authorizationHeader) {
        try {
            String accessToken = extractAccessTokenFromAuthorizationHeader(authorizationHeader);

            // Retrieve the RefreshToken based on the AccessToken
            Optional<RefreshToken> optionalRefreshToken = refreshTokenRepository.findByAccessToken(accessToken);
            if (optionalRefreshToken.isPresent()) {
                RefreshToken refreshToken = optionalRefreshToken.get();

                // Set the revoked status to true
                refreshToken.setRevoked(true);

                // Save the updated RefreshToken
                refreshTokenRepository.save(refreshToken);

                return "Logout successfully";
            } else {
                return "Invalid Access Token";
            }
        } catch (Exception e) {
            return "An error occurred while logging out";
        }
    }

    private String extractAccessTokenFromAuthorizationHeader(String authorizationHeader) {
        // Extract the AccessToken from the "Authorization" header (Bearer AccessToken)
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }
}