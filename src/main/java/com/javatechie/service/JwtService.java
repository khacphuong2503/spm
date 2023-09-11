package com.javatechie.service;

import io.jsonwebtoken.Claims;

import java.security.Key;
import java.util.*;
import java.util.function.Function;

public interface JwtService {
    String extractUsername(String token);
    Date extractExpiration(String token);
    <T> T extractClaim(String token, Function<Claims, T> claimsResolver);
    boolean isAccessTokenValid(String accessToken);
    String generateToken(String userName);
    String generateRefreshToken(String username);
    Key retrieveSignKey();
}