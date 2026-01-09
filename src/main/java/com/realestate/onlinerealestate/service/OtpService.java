package com.realestate.onlinerealestate.service;

import java.time.LocalDateTime;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.realestate.onlinerealestate.model.OtpVerification;
import com.realestate.onlinerealestate.repository.OtpRepository;

@Service
public class OtpService {

    @Autowired
    private OtpRepository otpRepository;

    @Transactional
    public String generateOtp(String email) {

        otpRepository.deleteByEmail(email); // ✅ now works (transaction)

        String otp = String.valueOf(100000 + new Random().nextInt(900000));

        OtpVerification verification = new OtpVerification();
        verification.setEmail(email);
        verification.setOtp(otp);
        verification.setExpiryTime(LocalDateTime.now().plusMinutes(5));

        otpRepository.save(verification);

        return otp;
    }

    public boolean verifyOtp(String email, String otp) {

        return otpRepository
                .findTopByEmailOrderByExpiryTimeDesc(email)
                .filter(o ->
                        o.getOtp().equals(otp) &&
                        o.getExpiryTime().isAfter(LocalDateTime.now())
                )
                .isPresent();
    }
}
