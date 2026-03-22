package com.example.demo.common.config;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;

@Component
@ConfigurationProperties(prefix = "sendgrid")
@Getter
@Setter
@Slf4j
public class SendGridProperties {
    private String apiKey;
    private String fromEmail;

    public void setApiKey(String apiKey) {
        // If from application.properties, use it; otherwise, try system property
        if (apiKey == null || apiKey.isEmpty() || apiKey.startsWith("${")) {
            this.apiKey = System.getenv("SENDGRID_API_KEY") != null 
                ? System.getenv("SENDGRID_API_KEY")
                : System.getProperty("SENDGRID_API_KEY", "");
        } else {
            this.apiKey = apiKey;
        }
    }

    public void setFromEmail(String fromEmail) {
        // If from application.properties, use it; otherwise, try system property
        if (fromEmail == null || fromEmail.isEmpty() || fromEmail.startsWith("${")) {
            this.fromEmail = System.getenv("SENDGRID_FROM_EMAIL") != null
                ? System.getenv("SENDGRID_FROM_EMAIL")
                : System.getProperty("SENDGRID_FROM_EMAIL", "");
        } else {
            this.fromEmail = fromEmail;
        }
    }

    @PostConstruct
    public void logConfiguration() {
        log.info("SendGrid Configuration: apiKey={}, fromEmail={}", 
            (apiKey != null && !apiKey.isEmpty() ? "***" + apiKey.substring(Math.max(0, apiKey.length()-10)) : "EMPTY"),
            fromEmail);
    }
}
