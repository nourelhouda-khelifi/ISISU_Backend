package com.example.demo.auth.infrastructure.service;

import com.example.demo.auth.domain.Utilisateur;
import com.example.demo.common.config.JwtUser;

public interface JwtService {

    String generateAccessToken(Utilisateur utilisateur);

    JwtUser parseAndValidateAccessToken(String token);

    long getAccessTokenTtlSeconds();
}
