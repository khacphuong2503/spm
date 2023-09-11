package com.javatechie.service.Impl;

import com.javatechie.entity.UserInfo;
import com.javatechie.repository.UserInfoRepository;
import com.javatechie.service.ResetPassService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class ResetPassServiceImpl implements ResetPassService {

    private final UserInfoRepository userInfoRepository;

    private final JavaMailSender javaMailSender;

    @Override
    @Transactional
    public String resetPassword(String email) {
        UserInfo user = userInfoRepository.findByEmail(email);

        if (user == null) {
            return "Invalid email.";
        }

        String newPassword = generateNewPassword();
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(newPassword);

        user.setPassword(hashedPassword);
        user.setPasswordReset(false);
        userInfoRepository.save(user);

        sendPasswordResetEmail(email, newPassword);

        return "Password reset successful. Please check your email for the new password.";
    }

    @Override
    @Transactional
    public String changePassword(String email, String password, String confirmPassword) {
        UserInfo user = userInfoRepository.findByEmail(email);

        if (user == null) {
            return "Invalid email.";
        }

        if (user.isChangePass()) {
            return "Password change not allowed.";
        }

        boolean isPasswordValid = validatePassword(password);

        if (!isPasswordValid) {
            return "Password must contain lowercase letters, uppercase letters, numbers, and special characters.";
        }

        if (!password.equals(confirmPassword)) {
            return "Password and confirm password do not match.";
        }

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(password);

        user.setPassword(hashedPassword);
        user.setPasswordReset(true);
        userInfoRepository.save(user);

        return "Password changed successfully.";
    }

    private boolean sendPasswordResetEmail(String email, String newPassword) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(email);
            helper.setSubject("Password Reset");

            String emailContent = "Your new password is: " + newPassword;

            helper.setText(emailContent);
            javaMailSender.send(message);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private String generateNewPassword() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder newPassword = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 8; i++) {
            int index = random.nextInt(characters.length());
            newPassword.append(characters.charAt(index));
        }

        return newPassword.toString();
    }

    private boolean validatePassword(String password) {
        String pattern = "(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=]).{8,}";
        return password.matches(pattern);
    }
}