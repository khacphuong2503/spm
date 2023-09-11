package com.javatechie.controller;

import com.javatechie.dto.ChangeEmailRequestDTO;
import com.javatechie.dto.ChangePasswordRequestDTO;
import com.javatechie.dto.ChangePhoneNumberRequestDTO;
import com.javatechie.dto.VerifyChangeEmailRequestDTO;
import com.javatechie.dto.VerifyChangePhoneNumberDTO;
import com.javatechie.entity.UserInfo;
import com.javatechie.repository.UserInfoRepository;
import com.javatechie.service.ChangeEmailService;
import com.javatechie.service.ChangePasswordService;
import com.javatechie.service.ChangePhoneNumberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/library")
public class UserController {
    private final ChangePasswordService changePasswordService;
    private final UserInfoRepository userInfoRepository;
    private final ChangeEmailService emailChangeService;
    private final ChangePhoneNumberService changePhoneNumberService;

    @PostMapping("/changePW")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @Validated
    public ResponseEntity<String> changePassword(@Valid @RequestBody ChangePasswordRequestDTO changePasswordRequest, Authentication authentication) {
        String result = changePasswordService.changePassword(changePasswordRequest, authentication);

        if (result.equals("User is not authenticated.")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
        } else if (result.equals("Invalid user.")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        } else if (result.equals("Invalid current password.")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        } else if (result.equals("New password and confirm password do not match.")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        } else if (result.equals("Password changed successfully. Please log in again with your new password.")) {
            return ResponseEntity.ok(result);
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred.");
    }

    @PostMapping("/changeEmail")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<String> changeEmail(@RequestBody ChangeEmailRequestDTO changeEmailRequest, Authentication authentication) {
        String currentUsername = authentication.getName();
        Optional<UserInfo> currentUserOptional = userInfoRepository.findByName(currentUsername);

        if (currentUserOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }

        UserInfo currentUser = currentUserOptional.get();
        String newEmail = changeEmailRequest.getEmail();

        String result = emailChangeService.updateEmail(currentUser, newEmail);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/verifyChangeEmail")
    public ResponseEntity<String> verifyChangeEmail(@RequestBody VerifyChangeEmailRequestDTO verifyChangeEmailRequest, Authentication authentication) {
        String currentUsername = authentication.getName();
        Optional<UserInfo> currentUserOptional = userInfoRepository.findByName(currentUsername);

        if (currentUserOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }

        UserInfo currentUser = currentUserOptional.get();

        String result = emailChangeService.verifyEmailChange(currentUser, verifyChangeEmailRequest);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/changePhoneNumber")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<String> changePhoneNumber(@RequestBody ChangePhoneNumberRequestDTO changePhoneNumberRequest, Authentication authentication) {
        String currentUsername = authentication.getName();
        Optional<UserInfo> currentUserOptional = userInfoRepository.findByName(currentUsername);

        if (currentUserOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }

        UserInfo currentUser = currentUserOptional.get();
        String newPhoneNumber = changePhoneNumberRequest.getPhoneNumber();

        String result = changePhoneNumberService.updatePhoneNumber(currentUser, newPhoneNumber);
        if (result.equals("Phone number change request initiated. Please verify your new phone number with the OTP code sent to your phone.")) {
            return ResponseEntity.ok(result);
        } else if (result.equals("Phone number already exists.") || result.equals("Invalid phone number format.")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    @PostMapping("/verifyChangePhoneNumber")
    public ResponseEntity<String> verifyChangePhoneNumber(@RequestBody VerifyChangePhoneNumberDTO verifyChangePhoneNumberRequest, Authentication authentication) {
        String currentUsername = authentication.getName();
        Optional<UserInfo> currentUserOptional = userInfoRepository.findByName(currentUsername);

        if (currentUserOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }

        UserInfo currentUser = currentUserOptional.get();

        String result = changePhoneNumberService.verifyPhoneNumberChange(currentUser, verifyChangePhoneNumberRequest);
        if (result.startsWith("Phone number change successful.")) {
            return ResponseEntity.ok(result);
        } else if (result.equals("Invalid phone number change request.") || result.equals("OTP code has expired. Please request a new OTP code.") || result.equals("Incorrect OTP code. Please enter the correct OTP code for the new phone number.")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }
}