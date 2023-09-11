package com.javatechie.service.Impl;

import com.javatechie.controller.errors.HttpException;
import com.javatechie.dto.AuthRequestDTO;
import com.javatechie.dto.JwtResponseDTO;
import com.javatechie.entity.RefreshToken;
import com.javatechie.entity.UserInfo;
import com.javatechie.repository.UserInfoRepository;
import com.javatechie.service.AuthenticationService;
import com.javatechie.service.JwtService;
import com.javatechie.service.LoginAttemptService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenServiceImpl refreshTokenService;
    private final UserInfoRepository userInfoRepository;
    private final LoginAttemptService loginAttemptService;

    @Override
    @Transactional
    public JwtResponseDTO authenticateAndGetToken(AuthRequestDTO authRequest) {

        if (loginAttemptService.isBlocked(authRequest.getUsername())) {
            throw new HttpException("Account is locked. Please try again after 30 minutes.", HttpStatus.LOCKED);
        }

        Optional<UserInfo> optionalUserInfo = userInfoRepository.findByName(authRequest.getUsername());

        UserInfo userInfo = optionalUserInfo.get();
        if (optionalUserInfo.isPresent()) {
//            UserInfo userInfo = optionalUserInfo.get();
            if (userInfo.getStatus().equals("blocked")) {
                throw new HttpException("Account is blocked. Please contact the administrator.", HttpStatus.FORBIDDEN);
            }
            if (userInfo.getStatus().equals("deleted")) {
                throw new HttpException("Account is deleted.", HttpStatus.FORBIDDEN);
            }
            if (!userInfo.isEmailVerified()) {
                throw new HttpException("The account has not been verified. Please verify the link in your email.", HttpStatus.UNAUTHORIZED);
            }
            if (!userInfo.isOtpVerified()) {
                throw new HttpException("The account has not been verified. Please verify the OTP in your email.", HttpStatus.UNAUTHORIZED);
            }
        } else {
            throw new HttpException("User not found.", HttpStatus.NOT_FOUND);
        }

        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
            if (authentication.isAuthenticated()) {
                loginAttemptService.resetAttempts(authRequest.getUsername());

//                UserInfo userInfo = optionalUserInfo.get();
                if (!userInfo.isPasswordReset()) {
                    userInfo.setChangePass(true);
                    userInfoRepository.save(userInfo);
                    throw new HttpException("Please change your password.", HttpStatus.UNAUTHORIZED);
                }

                String accessToken = jwtService.generateToken(authRequest.getUsername());
                RefreshToken refreshToken = refreshTokenService.createRefreshToken(authRequest.getUsername(), accessToken);
                return JwtResponseDTO.builder()
                        .accessToken(accessToken)
                        .token(refreshToken.getToken()).build();
            }
        } catch (AuthenticationException e) {
            loginAttemptService.addAttempt(authRequest.getUsername());

            if (loginAttemptService.isBlocked(authRequest.getUsername())) {
                throw new HttpException("Account is locked. Please try again after 30 minutes.", HttpStatus.FORBIDDEN);
            }
        } finally {
            System.out.println("Finally block executed.");
        }
        throw new HttpException("Invalid username or password.", HttpStatus.UNAUTHORIZED);
    }
}