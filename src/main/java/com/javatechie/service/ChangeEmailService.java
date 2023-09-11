package com.javatechie.service;

import com.javatechie.dto.VerifyChangeEmailRequestDTO;
import com.javatechie.entity.UserInfo;
import org.springframework.http.ResponseEntity;

public interface ChangeEmailService {
    String updateEmail(UserInfo currentUser, String newEmail);
    String verifyEmailChange(UserInfo currentUser, VerifyChangeEmailRequestDTO verifyChangeEmailRequest);
}
