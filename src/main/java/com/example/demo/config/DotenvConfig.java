package com.example.demo.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class to load environment variables from .env file
 */
@Configuration
public class DotenvConfig {

    static {
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing()
                .load();

        // Load specific environment variables from .env into system properties
        String[] envVars = {
                "DB_URL", "DB_USERNAME", "DB_PASSWORD",
                "SERVER_PORT", "SECURITY_USER_NAME",
                "SECURITY_USER_PASSWORD", "SECURITY_USER_ROLES",
                "SENDGRID_API_KEY", "SENDGRID_FROM_EMAIL"
        };

        for (String var : envVars) {
            String value = dotenv.get(var);
            if (value != null) {
                System.setProperty(var, value);
            }
        }
    }
}
