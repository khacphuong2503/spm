package com.javatechie.service.Impl;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.javatechie.dto.VerifyChangePhoneNumberDTO;
import com.javatechie.entity.UserInfo;
import com.javatechie.repository.UserInfoRepository;
import com.javatechie.service.ChangePhoneNumberService;
import com.javatechie.service.SmsVerificationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class ChangePhoneNumberServiceImpl implements ChangePhoneNumberService {

    private final UserInfoRepository userInfoRepository;
    private final SmsVerificationService smsVerificationService;

    @Override
    @Transactional
    public String updatePhoneNumber(UserInfo currentUser, String newPhoneNumber) {
        Optional<UserInfo> existingUserOptional = Optional.ofNullable(userInfoRepository.findByPhoneNumber(newPhoneNumber));
        if (existingUserOptional.isPresent()) {
            return "Phone number already exists.";
        }

        if (!isValidPhoneNumberFormat(newPhoneNumber)) {
            return "Invalid phone number format.";
        }

        String newOtp = generatePhoneOtp(); // Generate OTP for new phone number

        // Save the new phone number and current OTP
        currentUser.setNewPhoneNumber(newPhoneNumber);

        userInfoRepository.save(currentUser);

        // Save the new OTP and time for verification
        currentUser.setNewPhoneOtp(newOtp);
        currentUser.setNewOtpTime(LocalDateTime.now());

        userInfoRepository.save(currentUser);

        // Send verification SMS to the new phone number
        smsVerificationService.sendVerificationSms(newPhoneNumber, newOtp);

        return "Phone number change request initiated. Please verify your new phone number with the OTP code sent to your phone.";
    }

    public String verifyPhoneNumberChange(UserInfo currentUser, VerifyChangePhoneNumberDTO verifyChangePhoneNumberRequest) {
        if (currentUser.getNewPhoneOtp() == null) {
            return "Invalid phone number change request.";
        }

        String newPhoneNumber = currentUser.getPhoneNumber();
        String newPhoneOtp = currentUser.getNewPhoneOtp();
        String enteredNewOtp = verifyChangePhoneNumberRequest.getNewOtp();

        // Check if the OTP entered is correct
        if (enteredNewOtp.equals(newPhoneOtp)) {
            // Check if the OTP is still valid
            LocalDateTime newOtpTime = currentUser.getNewOtpTime();
            LocalDateTime currentTime = LocalDateTime.now();

            long newOtpExpirationSeconds = ChronoUnit.SECONDS.between(newOtpTime, currentTime);

            if (newOtpExpirationSeconds <= 60) {
                // Authentication successful, update new phone number
                currentUser.setPhoneNumber(currentUser.getNewPhoneNumber());
                currentUser.setNewPhoneOtp(null);
                currentUser.setNewOtpTime(null);

                userInfoRepository.save(currentUser);

                return "Phone number change successful. Your phone number has been updated to: " + currentUser.getNewPhoneNumber();
            } else {
                return "OTP code has expired. Please request a new OTP code.";
            }
        } else {
            return "Incorrect OTP code. Please enter the correct OTP code for the new phone number.";
        }
    }

    public String generatePhoneOtp() {
        int otpLength = 6;
        String allowedChars = "0123456789";

        StringBuilder otp = new StringBuilder();
        Random random = new Random();

        // Generate an OTP by randomly selecting characters from the string `allowedChars`
        for (int i = 0; i < otpLength; i++) {
            int randomIndex = random.nextInt(allowedChars.length());
            char randomChar = allowedChars.charAt(randomIndex);
            otp.append(randomChar);
        }

        return otp.toString();
    }

    private boolean isValidPhoneNumberFormat(String phoneNumber) {
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        try {
            Phonenumber.PhoneNumber parsedNumber = phoneNumberUtil.parse(phoneNumber, "your_default_country_code");
            return phoneNumberUtil.isValidNumber(parsedNumber);
        } catch (NumberParseException e) {
            return false;
        }
    }
}
