package com.javatechie.service.Impl;

import com.javatechie.dto.ChangePasswordRequestDTO;
import com.javatechie.entity.UserInfo;
import com.javatechie.repository.UserInfoRepository;
import com.javatechie.service.ChangePasswordService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChangePasswordServiceImpl implements ChangePasswordService {

    private final UserInfoRepository userInfoRepository;

    @Override
    @Transactional
    public String changePassword(ChangePasswordRequestDTO changePasswordRequest, Authentication authentication) {
        String username = authentication.getName();

        if (username == null) {
            return "User is not authenticated.";
        }

        Optional<UserInfo> optionalUser = userInfoRepository.findByName(username);

        if (optionalUser.isEmpty()) {
            return "Invalid user.";
        }

        UserInfo user = optionalUser.get();

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        if (!passwordEncoder.matches(changePasswordRequest.getCurrentPassword(), user.getPassword())) {
            return "Invalid current password.";
        }
        if (!changePasswordRequest.getNewPassword().equals(changePasswordRequest.getConfirmPassword())) {
            return "New password and confirm password do not match.";
        }

        String encodedNewPassword = passwordEncoder.encode(changePasswordRequest.getNewPassword());
        user.setPassword(encodedNewPassword);
        userInfoRepository.save(user);

        return "Password changed successfully. Please log in again with your new password.";
    }
}
