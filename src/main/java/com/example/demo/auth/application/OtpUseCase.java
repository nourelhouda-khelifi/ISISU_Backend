package com.example.demo.auth.application;

import com.example.demo.auth.domain.OtpCode;
import com.example.demo.auth.infrastructure.repository.OtpRepository;
import com.example.demo.auth.infrastructure.service.EmailService;
import com.example.demo.common.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OtpUseCase {

    private static final int OTP_LENGTH = 6;
    private static final int OTP_MAX_ATTEMPTS = 3;
    private static final int OTP_EXPIRATION_MINUTES = 10;

    private final OtpRepository otpRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void generateAndSendOtp(String email) {
        String normalizedEmail = normalizeEmail(email);

        otpRepository.invalidateActiveOtpsByEmail(normalizedEmail);

        String rawCode = randomNumericCode(OTP_LENGTH);
        OtpCode otp = OtpCode.builder()
                .email(normalizedEmail)
                .codeHash(passwordEncoder.encode(rawCode))
                .expireAt(LocalDateTime.now().plusMinutes(OTP_EXPIRATION_MINUTES))
                .utilise(false)
                .tentatives(0)
                .build();

        otpRepository.save(otp);
        emailService.sendOtpCode(normalizedEmail, rawCode);
    }

    @Transactional
    public void verifyOtpOrThrow(String email, String rawCode) {
        String normalizedEmail = normalizeEmail(email);
        OtpCode otp = otpRepository.findTopByEmailAndUtiliseFalseOrderByCreatedAtDesc(normalizedEmail)
            .orElseThrow(() -> new BadRequestException("Code OTP invalide ou expire."));

        LocalDateTime now = LocalDateTime.now();

        if (otp.isExpired(now)) {
            otp.setUtilise(true);
            otpRepository.save(otp);
            throw new BadRequestException("Code OTP expire. Demandez un nouveau code.");
        }

        if (otp.getTentatives() >= OTP_MAX_ATTEMPTS) {
            otp.setUtilise(true);
            otpRepository.save(otp);
            throw new BadRequestException("Trop de tentatives OTP. Demandez un nouveau code.");
        }

        if (!passwordEncoder.matches(rawCode, otp.getCodeHash())) {
            otp.setTentatives(otp.getTentatives() + 1);
            otp.setLastTriedAt(now);
            otpRepository.save(otp);
            throw new BadRequestException("Code OTP incorrect.");
        }

        otp.setUtilise(true);
        otp.setLastTriedAt(now);
        otpRepository.save(otp);
    }

    private String normalizeEmail(String email) {
        if (email == null) {
            throw new BadRequestException("Email obligatoire.");
        }
        return email.trim().toLowerCase();
    }

    private String randomNumericCode(int length) {
        int min = (int) Math.pow(10, length - 1);
        int max = (int) Math.pow(10, length) - 1;
        int value = new SecureRandom().nextInt(max - min + 1) + min;
        return String.valueOf(value);
    }
}
