package com.javatechie.service;

import com.javatechie.dto.ChangePasswordRequestDTO;
import com.javatechie.dto.VerifyChangePhoneNumberDTO;
import com.javatechie.entity.UserInfo;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

public interface ChangePasswordService {
    String changePassword(ChangePasswordRequestDTO changePasswordRequest, Authentication authentication);
}
