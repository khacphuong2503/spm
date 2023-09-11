package com.javatechie.repository;

import com.javatechie.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Integer> {
    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findByAccessToken(String accessToken);

    Optional<RefreshToken> findByUserInfoName(String username);

    void deleteByToken(String token);

}
