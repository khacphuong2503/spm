package com.javatechie.service.Impl;

import com.javatechie.dto.VerifyChangeEmailRequestDTO;
import com.javatechie.entity.UserInfo;
import com.javatechie.repository.UserInfoRepository;
import com.javatechie.service.ChangeEmailService;
import com.javatechie.service.EmailVerificationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class ChangeEmailServiceImpl implements ChangeEmailService {

    private final UserInfoRepository userInfoRepository;
    private final EmailVerificationService emailVerificationService;

    @Override
    @Transactional
    public String updateEmail(UserInfo currentUser, String newEmail) {
        Optional<UserInfo> existingUserOptional = Optional.ofNullable(userInfoRepository.findByEmail(newEmail));
        if (existingUserOptional.isPresent()) {
            return "Email already exists.";
        }

        if (!isValidEmailFormat(newEmail)) {
            return "Invalid email format.";
        }

        String newOtp = generateOtp(); // Generate OTP for new email

        // Save the new email and current OTP
        currentUser.setNewEmail(newEmail);

        userInfoRepository.save(currentUser);

        // Save the new OTP and time for verification
        currentUser.setNewOtp(newOtp);
        currentUser.setNewOtpTime(LocalDateTime.now());

        userInfoRepository.save(currentUser);

        // Send verification email to the new email address
        emailVerificationService.sendVerificationEmailNew(newEmail, newOtp);

        return "Email change request initiated. Please verify your new email address with the OTP code sent to your email.";
    }

    @Override
    @Transactional
    public String verifyEmailChange(UserInfo currentUser, VerifyChangeEmailRequestDTO verifyChangeEmailRequest) {
        if (currentUser.getNewOtp() == null) {
            return "Invalid email change request.";
        }

        String newEmail = currentUser.getEmail();

        String newOtp = currentUser.getNewOtp();

        String enteredNewOtp = verifyChangeEmailRequest.getNewOtp();

        // Check if the OTP code is correct
        if (enteredNewOtp.equals(newOtp)) {
            // Check if the OTP code is still valid
            LocalDateTime newOtpTime = currentUser.getNewOtpTime();
            LocalDateTime currentTime = LocalDateTime.now();

            long newOtpExpirationSeconds = ChronoUnit.SECONDS.between(newOtpTime, currentTime);

            if (newOtpExpirationSeconds <= 60) {
                // Verification successful, clear the temporary fields
                currentUser.setNewOtp(null);
                currentUser.setNewOtpTime(null);

                currentUser.setEmail(currentUser.getNewEmail());

                userInfoRepository.save(currentUser);

                return "Email change successful. Your email address has been updated to: " + currentUser.getNewEmail();
            } else {
                return "OTP code has expired. Please request a new OTP code.";
            }
        } else {
            return "Incorrect OTP code. Please enter the correct OTP code for the new email address.";
        }
    }

    private String generateOtp() {
        int otpLength = 6;

        StringBuilder otp = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < otpLength; i++) {
            int digit = random.nextInt(10);
            otp.append(digit);
        }

        return otp.toString();
    }

    private boolean isValidEmailFormat(String email) {
        boolean isValid = false;
        try {
            InternetAddress internetAddress = new InternetAddress(email);
            internetAddress.validate();
            isValid = true;
        } catch (AddressException e) {
            // Email is not valid
        }
        return isValid;
    }
}