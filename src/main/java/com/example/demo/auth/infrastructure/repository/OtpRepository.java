package com.example.demo.auth.infrastructure.repository;

import com.example.demo.auth.domain.OtpCode;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OtpRepository extends JpaRepository<OtpCode, Long> {

    Optional<OtpCode> findTopByEmailAndUtiliseFalseOrderByCreatedAtDesc(String email);

    @Modifying
    @Transactional
    @Query("update OtpCode o set o.utilise = true where o.email = :email and o.utilise = false")
    int invalidateActiveOtpsByEmail(@Param("email") String email);
}