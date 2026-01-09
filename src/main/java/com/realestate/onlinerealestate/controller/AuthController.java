package com.realestate.onlinerealestate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.realestate.onlinerealestate.dto.LoginRequest;
import com.realestate.onlinerealestate.dto.OtpRequest;
import com.realestate.onlinerealestate.dto.SignupRequest;
import com.realestate.onlinerealestate.model.User;
import com.realestate.onlinerealestate.repository.UserRepository;
import com.realestate.onlinerealestate.security.JwtUtil;
import com.realestate.onlinerealestate.service.EmailService;
import com.realestate.onlinerealestate.service.OtpService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OtpService otpService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private JwtUtil jwtUtil;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // =========================
    // SIGNUP → SEND OTP
    // =========================
    @PostMapping("/signup")
    public String signup(@RequestBody SignupRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return "Email already registered";
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setVerified(false);

        userRepository.save(user);

        String otp = otpService.generateOtp(request.getEmail());
        emailService.sendOtp(request.getEmail(), otp);

        return "OTP sent to email";
    }

    // =========================
    // VERIFY OTP → ACTIVATE USER
    // =========================
    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestBody OtpRequest request) {

        boolean valid = otpService.verifyOtp(
                request.getEmail(),
                request.getOtp()
        );

        if (!valid) {
            return "Invalid or expired OTP";
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setVerified(true);
        userRepository.save(user);

        return "Account verified successfully";
    }

    // =========================
    // LOGIN → JWT TOKEN
    // =========================
    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.isVerified()) {
            return "Please verify your account first";
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return "Invalid credentials";
        }

        return jwtUtil.generateToken(user.getEmail());
    }
}
