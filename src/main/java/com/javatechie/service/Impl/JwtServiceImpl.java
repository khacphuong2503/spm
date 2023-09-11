package com.javatechie.service.Impl;

import com.javatechie.entity.RefreshToken;
import com.javatechie.entity.UserInfo;
import com.javatechie.repository.RefreshTokenRepository;
import com.javatechie.repository.UserInfoRepository;
import com.javatechie.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    public static final String SECRET = "5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437";
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserInfoRepository userInfoRepository;
    long accessTokenExpirationSeconds = 1000 * 60 * 5;

    @Override
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    @Override
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    @Override
    public boolean isAccessTokenValid(String accessToken) {
        Optional<RefreshToken> refreshTokenOptional = refreshTokenRepository.findByAccessToken(accessToken);
        if (refreshTokenOptional.isPresent()) {
            RefreshToken refreshToken = refreshTokenOptional.get();
            // Valid AccessToken
            return !refreshToken.isRevoked();
        }
        // The AccessToken is invalid or the corresponding RefreshToken could not be found
        return false;
    }

    private UserInfo getUserInfoByUsername(String username) {
        Optional<UserInfo> userInfoOptional = userInfoRepository.findByName(username);
        if (userInfoOptional.isPresent()) {
            return userInfoOptional.get();
        } else {
            throw new UsernameNotFoundException("Người dùng không tồn tại!");
        }
    }

    @Override
    public String generateToken(String userName) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userName);
    }

    @Override
    public String generateRefreshToken(String username) {
        // Generate a unique refresh token
        String refreshToken = UUID.randomUUID().toString();

        // Save the refresh token to the database or any other storage mechanism if needed

        return refreshToken;
    }

    private String createToken(Map<String, Object> claims, String userName) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        Date now = calendar.getTime();
        Date expirationDate = new Date(now.getTime() + accessTokenExpirationSeconds * 1000);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userName)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 *300000))
                .signWith(getSignKey(), SignatureAlgorithm.HS256).compact();
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public Key retrieveSignKey() {
        return getSignKey();
    }
}
