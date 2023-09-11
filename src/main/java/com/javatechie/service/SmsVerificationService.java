package com.javatechie.service;

public interface SmsVerificationService {
    void sendVerificationSms(String phoneNumber, String otp);

    boolean verifyOTP(String email, String newOtp);
}
