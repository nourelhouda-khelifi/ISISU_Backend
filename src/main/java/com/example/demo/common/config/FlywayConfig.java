package com.example.demo.common.config;

import org.flywaydb.core.Flyway;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class FlywayConfig {

    @Bean
    public Flyway flyway(DataSource dataSource) {
        return Flyway.configure()
                .dataSource(dataSource)
                .baselineOnMigrate(true)
                .load();
    }

    @Bean
    public CommandLineRunner flywayRepairAndMigrate(Flyway flyway) {
        return args -> {
            flyway.repair();
            flyway.migrate();
        };
    }
}
