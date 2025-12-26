package com.realestate.onlinerealestate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.realestate.onlinerealestate.com.EmailService;
import com.realestate.onlinerealestate.dto.LoginRequest;
import com.realestate.onlinerealestate.dto.OtpRequest;
import com.realestate.onlinerealestate.service.OtpService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    @Autowired
    private OtpService otpService;

    @Autowired
    private EmailService emailService;

    // STEP 1: Login & Send OTP
    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {
        String otp = otpService.generateOtp(request.getEmail());
        emailService.sendOtp(request.getEmail(), otp);
        return "OTP sent to email";
    }

    // STEP 2: Verify OTP
    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestBody OtpRequest request) {
        boolean isValid = otpService.verifyOtp(request.getEmail(), request.getOtp());
        return isValid ? "Login successful" : "Invalid or expired OTP";
    }
}
