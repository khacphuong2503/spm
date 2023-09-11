package com.javatechie.service.Impl;

import com.javatechie.config.UserNotFoundException;
import com.javatechie.entity.UserInfo;
import com.javatechie.repository.UserInfoRepository;
import com.javatechie.service.EmailVerificationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailVerificationServiceImpl implements EmailVerificationService {
    private final UserInfoRepository userInfoRepository;
    private final JavaMailSender javaMailSender;


    @Override
    @Transactional
    public void sendVerificationEmail(String email, String currentOtp) {
        // Save the OTP codes to the user's information if the user exists
        UserInfo user = userInfoRepository.findByEmail(email);
        if (user != null) {
            LocalDateTime currentOtpExpirationTime = LocalDateTime.now().plusSeconds(60);
//            user.setCurrentOtpTime(currentOtpExpirationTime);
            userInfoRepository.save(user);

            // Send verification emails to the provided email address with the OTP codes
            String currentSubject = "Current Email Verification";
            String currentMessage = "Your OTP code for the current email is: " + currentOtp;
            sendEmail(email, currentSubject, currentMessage);
        } else {
            // Handle user not found case
            throw new UserNotFoundException("User not found for email: " + email);
        }
    }

    @Override
    @Transactional
    public void sendVerificationEmailNew(String email, String newOtp) {
        UserInfo user = new UserInfo();
        String newSubject = "New Email Verification";
        String newMessage = "Your OTP code for the new email is: " + newOtp;
        sendEmail(email, newSubject, newMessage);
    }

    @Override
    public boolean verifyOTP(String email, String currentOtp, String newOtp) {
        // Check the verification OTP codes
        UserInfo user = userInfoRepository.findByEmail(email);
        return user != null && user.getNewOtp().equals(newOtp);
    }

    private void sendEmail(String email, String subject, String message) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(email);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);
        javaMailSender.send(mailMessage);
    }

    private String generateOTP() {
        // Generate a random OTP code, for example: 6 digits
        int otpLength = 6;
        Random random = new Random();
        StringBuilder otp = new StringBuilder();

        for (int i = 0; i < otpLength; i++) {
            otp.append(random.nextInt(10));
        }

        return otp.toString();
    }

    @Override
    public void sendEmail(String email, String subject, String message, String imageUrl) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setTo(email);
            helper.setSubject(subject);

            // Tạo nội dung email có hình ảnh sách
            String content = "<html><body><p>" + message + "</p><br><img src='" + imageUrl + "'/></body></html>";
            helper.setText(content, true);

            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            // Xử lý ngoại lệ nếu gửi email thất bại
            throw new RuntimeException("Failed to send email", e);
        }
    }
}