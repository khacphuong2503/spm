package com.javatechie.service;

import com.javatechie.dto.UserInfoDTO;
import com.javatechie.entity.UserInfo;
import org.springframework.http.ResponseEntity;

public interface RegisterService {
    RegistrationResult addNewUser(UserInfoDTO userInfoDTO);
    String verifyOTP(String email, String verificationCode);
    String verifyEmail(String email, String verificationCode);
    String resendVerificationLink(String email);
}
