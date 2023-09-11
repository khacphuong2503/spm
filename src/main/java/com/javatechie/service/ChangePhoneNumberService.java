package com.javatechie.service;

import com.javatechie.dto.VerifyChangeEmailRequestDTO;
import com.javatechie.dto.VerifyChangePhoneNumberDTO;
import com.javatechie.entity.UserInfo;
import jakarta.transaction.Transactional;

public interface ChangePhoneNumberService {
    @Transactional
    String updatePhoneNumber(UserInfo currentUser, String newPhoneNumber);

    String verifyPhoneNumberChange(UserInfo currentUser, VerifyChangePhoneNumberDTO verifyChangeEmailRequest);
}
