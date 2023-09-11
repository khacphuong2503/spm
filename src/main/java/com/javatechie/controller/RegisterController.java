package com.javatechie.controller;

import com.javatechie.dto.UserInfoDTO;
import com.javatechie.service.RegisterService;
import com.javatechie.service.RegistrationResult;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/library")
@AllArgsConstructor
public class RegisterController {

    private final RegisterService registerService;

    @PostMapping("/signUp")
    public ResponseEntity<RegistrationResult> addNewUser(@Valid @RequestBody UserInfoDTO userInfoDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder errors = new StringBuilder();
            for (ObjectError error : bindingResult.getAllErrors()) {
                errors.append(error.getDefaultMessage()).append("\n");
            }
            RegistrationResult result = new RegistrationResult(false, errors.toString());
            return ResponseEntity.badRequest().body(result);
        }

        RegistrationResult result = registerService.addNewUser(userInfoDTO);
        if (result.isSuccess()) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    @GetMapping("/verifyOTP")
    public ResponseEntity<String> verifyOTP(@RequestParam("email") String email, @RequestParam("otp") String verificationCode) {
        String response = registerService.verifyOTP(email, verificationCode);
        if (response.equals("OTP verification successful. Email verified.")) {
            return ResponseEntity.ok(response);
        } else if (response.equals("OTP verification failed.")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @GetMapping("/verifyEmail")
    public ResponseEntity<String> verifyEmail(@RequestParam("email") String email, @RequestParam("code") String verificationCode) {
        String response = registerService.verifyEmail(email, verificationCode);
        if (response.equals("Verify email link is successful. Email verified.")) {
            return ResponseEntity.ok(response);
        } else if (response.equals("Verify email link is invalid.")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @GetMapping("/resendVerificationLink")
    public ResponseEntity<String> resendVerificationLink(@RequestParam("email") String email) {
        String response = registerService.resendVerificationLink(email);
        if (response.equals("Verification email resent successfully.")) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}