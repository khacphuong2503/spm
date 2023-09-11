package com.javatechie.service;

import org.springframework.http.ResponseEntity;

public interface LogOutService {
    String logout(String authorizationHeader);
}
