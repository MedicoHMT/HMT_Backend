package com.example.hmt.core.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender javaMailSender;

    @Async
    public void sendOtpEmail(String toEmail, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(toEmail);
        message.setSubject("Your OTP Code");
        message.setText("Hello,\n\nYour One-Time Password (OTP) for login is: " + otp + "\n\nThis code expires in 5 minutes.");
        message.setFrom("hmtpriyanshu@gmail.com");

        javaMailSender.send(message);
    }
}
