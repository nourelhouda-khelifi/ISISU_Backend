package com.example.demo.auth.infrastructure.service;

public interface EmailService {

    void sendOtpCode(String email, String code);
}
