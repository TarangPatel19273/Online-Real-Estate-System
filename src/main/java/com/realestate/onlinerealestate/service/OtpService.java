package com.realestate.onlinerealestate.service;

import java.time.LocalDateTime;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.realestate.onlinerealestate.model.OtpVerification;
import com.realestate.onlinerealestate.repository.OtpRepository;

@Service
public class OtpService {

    @Autowired
    private OtpRepository otpRepository;

    // Generate OTP
    public String generateOtp(String email) {

        // ✅ DELETE OLD OTPs FIRST
        otpRepository.deleteByEmail(email);

        String otp = String.valueOf(new Random().nextInt(900000) + 100000);

        OtpVerification otpVerification = new OtpVerification();
        otpVerification.setEmail(email);
        otpVerification.setOtp(otp);
        otpVerification.setExpiryTime(LocalDateTime.now().plusMinutes(5));

        otpRepository.save(otpVerification);
        return otp;
    }

    // Verify OTP
    public boolean verifyOtp(String email, String otp) {

        return otpRepository.findTopByEmailOrderByExpiryTimeDesc(email)
                .filter(data -> data.getOtp().equals(otp))
                .filter(data -> data.getExpiryTime().isAfter(LocalDateTime.now()))
                .isPresent();
    }
}
