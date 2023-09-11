package com.javatechie.service;

public interface ResetPassService {
    String resetPassword(String email);
    String changePassword(String email, String password, String confirmPassword);
}
