package com.javatechie.service;


public interface LoginAttemptService {
    void addAttempt(String name);
    void resetAttempts(String name);
    boolean isBlocked(String name);
}