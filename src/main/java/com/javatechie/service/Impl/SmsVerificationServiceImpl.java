package com.javatechie.service.Impl;

import com.javatechie.entity.UserInfo;
import com.javatechie.service.SmsVerificationService;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.javatechie.repository.UserInfoRepository;
import com.twilio.type.PhoneNumber;
import org.springframework.stereotype.Service;

@Service
public class SmsVerificationServiceImpl implements SmsVerificationService {
    UserInfoRepository userInfoRepository;

    private final String ACCOUNT_SID = "ACdcd1fdc8b72f909814c6e38ec9ce1ed2";
    private final String AUTH_TOKEN = "a97c3c706d5cb03f5737a7722389578f";
    private final String FROM_PHONE_NUMBER = "+18149759493";

    public SmsVerificationServiceImpl() {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    }

    @Override
    public void sendVerificationSms(String phoneNumber, String otp) {
        try {
            Message message = Message.creator(
                            new PhoneNumber(phoneNumber),
                            new PhoneNumber(FROM_PHONE_NUMBER),
                            "Your verification code is: " + otp)
                    .create();

            System.out.println("SMS sent with SID: " + message.getSid());
        } catch (Exception e) {
            System.out.println("Failed to send SMS: " + e.getMessage());
        }
    }

    @Override
    public boolean verifyOTP(String email, String newOtp) {
        // Check the verification OTP codes
        UserInfo user = userInfoRepository.findByEmail(email);
        return user != null && user.getNewOtp().equals(newOtp);
    }
}
