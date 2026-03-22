package com.example.demo.auth.api;

import com.example.demo.auth.api.dto.AuthMeResponse;
import com.example.demo.auth.api.dto.InscriptionFIE3Request;
import com.example.demo.auth.api.dto.InscriptionResponse;
import com.example.demo.auth.api.dto.InscriptionVAERequest;
import com.example.demo.auth.api.dto.LoginRequest;
import com.example.demo.auth.api.dto.LoginResponse;
import com.example.demo.auth.api.dto.OtpResponse;
import com.example.demo.auth.api.dto.RenvoyerOtpRequest;
import com.example.demo.auth.api.dto.VerifierOtpRequest;
import com.example.demo.auth.application.InscriptionFIE3UseCase;
import com.example.demo.auth.application.InscriptionVAEUseCase;
import com.example.demo.auth.application.LoginUseCase;
import com.example.demo.auth.application.RenvoyerOtpUseCase;
import com.example.demo.auth.application.VerifierOtpUseCase;
import com.example.demo.common.config.JwtUser;
import com.example.demo.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final InscriptionFIE3UseCase inscriptionFIE3UseCase;
    private final InscriptionVAEUseCase inscriptionVAEUseCase;
    private final VerifierOtpUseCase verifierOtpUseCase;
    private final RenvoyerOtpUseCase renvoyerOtpUseCase;
    private final LoginUseCase loginUseCase;

    @PostMapping("/inscription/fie3")
    public ResponseEntity<ApiResponse<InscriptionResponse>> inscriptionFie3(@Valid @RequestBody InscriptionFIE3Request request) {
        InscriptionResponse response = inscriptionFIE3UseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED.value(), "Inscription FIE3 effectuee", response));
    }

    @PostMapping("/inscription/vae")
    public ResponseEntity<ApiResponse<InscriptionResponse>> inscriptionVae(@Valid @RequestBody InscriptionVAERequest request) {
        InscriptionResponse response = inscriptionVAEUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED.value(), "Inscription VAE effectuee", response));
    }

    @PostMapping("/otp/verifier")
    public ResponseEntity<ApiResponse<InscriptionResponse>> verifierOtp(@Valid @RequestBody VerifierOtpRequest request) {
        InscriptionResponse response = verifierOtpUseCase.execute(request);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "OTP verifie", response));
    }

    @PostMapping("/otp/renvoyer")
    public ResponseEntity<ApiResponse<OtpResponse>> renvoyerOtp(@Valid @RequestBody RenvoyerOtpRequest request) {
        OtpResponse response = renvoyerOtpUseCase.execute(request);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "OTP renvoye", response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = loginUseCase.execute(request);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Connexion reussie", response));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<AuthMeResponse>> me(@AuthenticationPrincipal JwtUser jwtUser, Authentication authentication) {
        JwtUser principal = jwtUser;

        if (principal == null && authentication != null && authentication.getPrincipal() instanceof JwtUser p) {
            principal = p;
        }

        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), "Utilisateur non authentifie", null));
        }

        AuthMeResponse response = new AuthMeResponse(principal.userId(), principal.email(), principal.role());
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Profil courant", response));
    }
}
