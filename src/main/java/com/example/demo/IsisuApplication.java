package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling  // ✅ FIX M2: Activer les scheduled tasks (SessionCleanupScheduler)
public class IsisuApplication {

	public static void main(String[] args) {
		SpringApplication.run(IsisuApplication.class, args);
	}

}
