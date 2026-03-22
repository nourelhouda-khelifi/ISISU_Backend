package com.example.demo.auth.infrastructure.service;

import com.example.demo.common.config.SendGridProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.HttpEntity;

@Slf4j
@Service
@Primary
public class SendGridEmailService implements EmailService {

    private final RestTemplate restTemplate;
    private final String apiKey;
    private final String fromEmail;
    private final ObjectMapper objectMapper;
    private static final String SENDGRID_API_URL = "https://api.sendgrid.com/v3/mail/send";

    public SendGridEmailService(RestTemplate restTemplate, SendGridProperties sendGridProperties) {
        this.restTemplate = restTemplate;
        this.apiKey = sendGridProperties.getApiKey();
        this.fromEmail = sendGridProperties.getFromEmail();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void sendOtpCode(String email, String code) {
        try {
            String emailBody = buildOtpEmailHtml(code);
            String requestBody = buildSendGridRequest(email, emailBody);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);

            HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
            var response = restTemplate.postForEntity(SENDGRID_API_URL, request, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Email OTP envoyé avec succès à: {}", email);
            } else {
                log.error("Erreur lors de l'envoi de l'email. Code: {}", response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi de l'email OTP à {}", email, e);
            throw new RuntimeException("Impossible d'envoyer l'email OTP", e);
        }
    }

    private String buildSendGridRequest(String recipientEmail, String htmlContent) throws Exception {
        ObjectNode root = objectMapper.createObjectNode();

        // From
        ObjectNode from = objectMapper.createObjectNode();
        from.put("email", fromEmail);
        from.put("name", "ISISU");
        root.set("from", from);

        // Subject
        root.put("subject", "Votre code OTP - ISISU");

        // Personalization
        ArrayNode personalizations = objectMapper.createArrayNode();
        ObjectNode personalization = objectMapper.createObjectNode();
        ArrayNode to = objectMapper.createArrayNode();
        ObjectNode toEmail = objectMapper.createObjectNode();
        toEmail.put("email", recipientEmail);
        to.add(toEmail);
        personalization.set("to", to);
        personalizations.add(personalization);
        root.set("personalizations", personalizations);

        // Content
        ArrayNode content = objectMapper.createArrayNode();
        ObjectNode htmlContent_ = objectMapper.createObjectNode();
        htmlContent_.put("type", "text/html");
        htmlContent_.put("value", htmlContent);
        content.add(htmlContent_);
        root.set("content", content);

        return objectMapper.writeValueAsString(root);
    }

    private String buildOtpEmailHtml(String code) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; }
                    .container { max-width: 500px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #007bff; color: white; padding: 20px; border-radius: 5px; text-align: center; }
                    .content { padding: 20px; background-color: #f9f9f9; border-radius: 5px; margin: 20px 0; }
                    .otp-code { font-size: 32px; font-weight: bold; color: #007bff; text-align: center; letter-spacing: 5px; }
                    .footer { text-align: center; color: #666; font-size: 12px; margin-top: 20px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>ISISU - Vérification d'email</h1>
                    </div>
                    <div class="content">
                        <p>Bonjour,</p>
                        <p>Vous avez demandé une vérification de votre adresse email. Voici votre code OTP :</p>
                        <div class="otp-code">%s</div>
                        <p>Ce code est valide pendant 10 minutes.</p>
                        <p>Si vous n'avez pas demandé ce code, ignorez cet email.</p>
                    </div>
                    <div class="footer">
                        <p>© 2026 ISISU. Tous droits réservés.</p>
                    </div>
                </div>
            </body>
            </html>
            """, code);
    }
}
