package com.example.demo.referentiel.infrastructure;

import com.example.demo.referentiel.domain.UniteEnseignement;
import com.example.demo.referentiel.domain.enums.Semestre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UniteEnseignementRepository extends JpaRepository<UniteEnseignement, Long> {
    List<UniteEnseignement> findBySemestre(Semestre semestre);
}
