package com.example.demo.referentiel.presentation;

import com.example.demo.referentiel.application.ReferentielService;
import com.example.demo.referentiel.domain.enums.Semestre;
import com.example.demo.referentiel.presentation.dto.CompetenceDTO;
import com.example.demo.referentiel.presentation.dto.ModuleFIEDTO;
import com.example.demo.referentiel.presentation.dto.UniteEnseignementDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/referentiel")
@RequiredArgsConstructor
@Tag(name = "Référentiel FIE3", description = "Accès au référentiel des modules et compétences du cursus FIE3")
@SecurityRequirement(name = "Bearer Authentication")
public class ReferentielController {
    
    private final ReferentielService referentielService;
    
    /**
     * GET /api/v1/referentiel/semestres
     * Récupérer les deux semestres (S5, S6)
     */
    @GetMapping("/semestres")
    @Operation(summary = "Lister les semestres", description = "Récupère les semestres disponibles (S5, S6)")
    public ResponseEntity<List<Object>> getSemestres() {
        log.info("GET /referentiel/semestres");
        return ResponseEntity.ok(referentielService.getAllSemestres());
    }
    
    /**
     * GET /api/v1/referentiel/semestres/{semestre}/unites
     * Récupérer toutes les UEs d'un semestre
     */
    @GetMapping("/semestres/{semestre}/unites")
    @Operation(summary = "Lister les UEs d'un semestre", description = "Récupère toutes les unités d'enseignement d'un semestre")
    public ResponseEntity<List<UniteEnseignementDTO>> getUnitesBySemestre(
            @PathVariable String semestre) {
        log.info("GET /referentiel/semestres/{}/unites", semestre);
        Semestre sem = Semestre.valueOf(semestre.toUpperCase());
        return ResponseEntity.ok(referentielService.getUnitesBySemestre(sem));
    }
    
    /**
     * GET /api/v1/referentiel/modules
     * Récupérer tous les modules évaluables
     */
    @GetMapping("/modules")
    @Operation(summary = "Lister tous les modules évaluables", description = "Récupère tous les modules FIE3 qui sont évalués")
    public ResponseEntity<List<ModuleFIEDTO>> getAllModules() {
        log.info("GET /referentiel/modules");
        return ResponseEntity.ok(referentielService.getAllEvaluableModules());
    }
    
    /**
     * GET /api/v1/referentiel/semestres/{semestre}/modules
     * Récupérer tous les modules évaluables d'un semestre
     */
    @GetMapping("/semestres/{semestre}/modules")
    @Operation(summary = "Lister les modules d'un semestre", description = "Récupère tous les modules évaluables d'un semestre")
    public ResponseEntity<List<ModuleFIEDTO>> getModulesBySemestre(
            @PathVariable String semestre) {
        log.info("GET /referentiel/semestres/{}/modules", semestre);
        Semestre sem = Semestre.valueOf(semestre.toUpperCase());
        return ResponseEntity.ok(referentielService.getModulesBySemestre(sem));
    }
    
    /**
     * GET /api/v1/referentiel/modules/{id}
     * Récupérer un module avec toutes ses compétences et prérequis
     */
    @GetMapping("/modules/{id}")
    @Operation(summary = "Détail complet d'un module", description = "Récupère un module avec ses compétences, prérequis et volumes horaires")
    public ResponseEntity<ModuleFIEDTO> getModuleById(@PathVariable Long id) {
        log.info("GET /referentiel/modules/{}", id);
        return ResponseEntity.ok(referentielService.getModuleById(id));
    }
    
    /**
     * GET /api/v1/referentiel/modules/code/{code}
     * Récupérer un module par son code (E3-1-IN-1, etc.)
     */
    @GetMapping("/modules/code/{code}")
    @Operation(summary = "Détail d'un module par code", description = "Récupère un module en utilisant son code ISIS (ex: E3-1-IN-1)")
    public ResponseEntity<ModuleFIEDTO> getModuleByCode(@PathVariable String code) {
        log.info("GET /referentiel/modules/code/{}", code);
        return ResponseEntity.ok(referentielService.getModuleByCode(code));
    }
    
    /**
     * GET /api/v1/referentiel/modules/{id}/competences
     * Récupérer les compétences d'un module (ordonnées par syllabus)
     */
    @GetMapping("/modules/{moduleId}/competences")
    @Operation(summary = "Compétences d'un module", description = "Récupère toutes les compétences d'un module, ordonnées par le syllabus")
    public ResponseEntity<List<CompetenceDTO>> getCompetencesByModule(
            @PathVariable Long moduleId) {
        log.info("GET /referentiel/modules/{}/competences", moduleId);
        return ResponseEntity.ok(referentielService.getCompetencesByModule(moduleId));
    }

    /**
     * GET /api/v1/referentiel/competences
     * Récupérer toutes les compétences
     */
    @GetMapping("/competences")
    @Operation(summary = "Lister toutes les compétences", description = "Récupère les 42 compétences du syllabus FIE3")
    public ResponseEntity<List<CompetenceDTO>> getAllCompetences() {
        log.info("GET /referentiel/competences");
        return ResponseEntity.ok(referentielService.getAllCompetences());
    }
    
    /**
     * GET /api/v1/referentiel/competences/{id}
     * Récupérer une compétence par ID
     */
    @GetMapping("/competences/{id}")
    @Operation(summary = "Détail d'une compétence", description = "Récupère les détails d'une compétence (intitulé, description, poids, niveau)")
    public ResponseEntity<CompetenceDTO> getCompetence(@PathVariable Long id) {
        log.info("GET /referentiel/competences/{}", id);
        return ResponseEntity.ok(referentielService.getCompetence(id));
    }
}
