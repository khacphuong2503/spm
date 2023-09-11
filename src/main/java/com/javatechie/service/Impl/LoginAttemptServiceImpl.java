package com.javatechie.service.Impl;

import com.javatechie.entity.UserInfo;
import com.javatechie.repository.UserInfoRepository;
import com.javatechie.service.LoginAttemptService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoginAttemptServiceImpl implements LoginAttemptService {
    private final int MAX_ATTEMPTS = 3;
    private final int LOCK_DURATION_MINUTES = 30;

    private final UserInfoRepository userInfoRepository;


    @Override
    @Transactional
    public void addAttempt(String name) {
        Optional<UserInfo> userInfoOptional = userInfoRepository.findByName(name);
        if (userInfoOptional.isEmpty()) {
            return;
        }

        UserInfo userInfo = userInfoOptional.get();
        int attempts = userInfo.getLoginAttempts() + 1;
        userInfo.setLoginAttempts(attempts);

        if (attempts >= MAX_ATTEMPTS) {
            LocalDateTime lockExpiration = LocalDateTime.now().plusMinutes(LOCK_DURATION_MINUTES);
            userInfo.setStatus("blocked");
            userInfo.setBlockExpiration(lockExpiration);
        }

        userInfoRepository.save(userInfo);
    }

    @Override
    @Transactional
    public void resetAttempts(String name) {
        Optional<UserInfo> userInfoOptional = userInfoRepository.findByName(name);
        if (userInfoOptional.isPresent()) {
            UserInfo userInfo = userInfoOptional.get();
            userInfo.setLoginAttempts(0);
            userInfoRepository.save(userInfo);
        }
    }

    @Override
    @Transactional
    public boolean isBlocked(String name) {
        Optional<UserInfo> userInfoOptional = userInfoRepository.findByName(name);
        if (userInfoOptional.isEmpty() || !"blocked".equals(userInfoOptional.get().getStatus())) {
            return false;
        }

        UserInfo userInfo = userInfoOptional.get();
        if (userInfo.getBlockExpiration().isAfter(LocalDateTime.now())) {
            return true;
        } else {
            userInfo.setStatus("active"); // Update the status to "active"
            userInfoRepository.save(userInfo);
            return false;
        }
    }
}
