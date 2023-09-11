package com.javatechie.service.Impl;

import com.javatechie.dto.JwtResponseDTO;
import com.javatechie.dto.RefreshTokenRequestDTO;
import com.javatechie.entity.RefreshToken;
import com.javatechie.entity.UserInfo;
import com.javatechie.repository.RefreshTokenRepository;
import com.javatechie.repository.UserInfoRepository;
import com.javatechie.service.JwtService;
import com.javatechie.service.RefreshTokenService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserInfoRepository userInfoRepository;

    @Override
    @Transactional
    public JwtResponseDTO refreshToken(RefreshTokenRequestDTO refreshTokenRequest) {
        return findByToken(refreshTokenRequest.getToken())
                .map(this::verifyExpiration)
                .map(refreshToken -> {
                    if (refreshToken.isRevoked()) {
                        throw new RuntimeException("Refresh token is revoked!");
                    }
                    UserInfo userInfo = refreshToken.getUserInfo();
                    String newAccessToken = jwtService.generateToken(userInfo.getName());
                    String newRefreshToken = jwtService.generateRefreshToken(userInfo.getName());

                    // Update the expiry date of the new RefreshToken
                    Instant newRefreshTokenExpiry = refreshToken.getExpiryDate().plus(Duration.ofDays(30)); // Example: Set expiry to 30 days from now
                    refreshToken.setExpiryDate(newRefreshTokenExpiry);
                    refreshToken.setAccessToken(newAccessToken);
                    refreshTokenRepository.save(refreshToken);

                    // Create and save the new RefreshToken
                    RefreshToken newRefreshTokenEntity = new RefreshToken();
                    newRefreshTokenEntity.setToken(newRefreshToken);
                    newRefreshTokenEntity.setExpiryDate(Instant.now().plus(Duration.ofDays(30))); // Example: Set expiry to 30 days from now
                    newRefreshTokenEntity.setUserInfo(userInfo);
                    refreshTokenRepository.save(newRefreshTokenEntity);

                    return JwtResponseDTO.builder()
                            .accessToken(newAccessToken)
                            .token(newRefreshToken)
                            .build();
                })
                .orElseThrow(() -> new RuntimeException("Refresh token is not in the database!"));
    }

    private Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    private RefreshToken verifyExpiration(RefreshToken refreshToken) {
        if (refreshToken.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(refreshToken);
            throw new RuntimeException(refreshToken.getToken() + " Refresh token was expired. Please make a new signin request");
        }
        return refreshToken;
    }

    public RefreshToken createRefreshToken(String username, String accessToken) {
        Instant expiryDate = Instant.now().plus(Duration.ofDays(10)).atZone(ZoneId.of("Asia/Ho_Chi_Minh")).toInstant();

        RefreshToken refreshToken = RefreshToken.builder()
                .userInfo(userInfoRepository.findByName(username).get())
                .token(UUID.randomUUID().toString())
                .expiryDate(expiryDate)
                .accessToken(accessToken)
                .isRevoked(false)
                .build();
        refreshTokenRepository.save(refreshToken);
        return refreshToken;
    }
}