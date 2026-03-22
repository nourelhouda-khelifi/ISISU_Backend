package com.example.demo.auth.infrastructure.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ConsoleEmailService implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(ConsoleEmailService.class);

    @Override
    public void sendOtpCode(String email, String code) {
        log.info("[OTP] Code envoye a {}: {}", email, code);
    }
}
