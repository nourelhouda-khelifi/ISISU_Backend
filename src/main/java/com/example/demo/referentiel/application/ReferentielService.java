package com.example.demo.referentiel.application;

import com.example.demo.common.exception.NotFoundException;
import com.example.demo.referentiel.domain.Competence;
import com.example.demo.referentiel.domain.ModuleFIE;
import com.example.demo.referentiel.domain.UniteEnseignement;
import com.example.demo.referentiel.domain.enums.Semestre;
import com.example.demo.referentiel.infrastructure.CompetenceRepository;
import com.example.demo.referentiel.infrastructure.ModuleFIERepository;
import com.example.demo.referentiel.infrastructure.UniteEnseignementRepository;
import com.example.demo.referentiel.presentation.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReferentielService {
    
    private final UniteEnseignementRepository uniteEnseignementRepository;
    private final ModuleFIERepository moduleFIERepository;
    private final CompetenceRepository competenceRepository;
    
    /**
     * Récupérer tous les semestres avec leurs UEs complets
     */
    public List<Object> getAllSemestres() {
        log.info("Fetching all semestres");
        return List.of(Semestre.S5, Semestre.S6);
    }
    
    /**
     * Récupérer une UE par ID
     */
    public UniteEnseignementDTO getUniteEnseignement(Long id) {
        log.info("Fetching UE with ID: {}", id);
        UniteEnseignement ue = uniteEnseignementRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Unité d'enseignement not found with id: " + id));
        return convertToUniteEnseignementDTO(ue);
    }
    
    /**
     * Récupérer toutes les UEs d'un semestre
     */
    public List<UniteEnseignementDTO> getUnitesBySemestre(Semestre semestre) {
        log.info("Fetching UEs for semestre: {}", semestre);
        return uniteEnseignementRepository.findBySemestre(semestre)
                .stream()
                .map(this::convertToUniteEnseignementDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Récupérer un module par ID avec tous ses détails (compétences, prérequis)
     */
    public ModuleFIEDTO getModuleById(Long id) {
        log.info("Fetching module with ID: {}", id);
        ModuleFIE module = moduleFIERepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Module not found with id: " + id));
        return convertToModuleFIEDTO(module);
    }
    
    /**
     * Récupérer un module par son code
     */
    public ModuleFIEDTO getModuleByCode(String code) {
        log.info("Fetching module with code: {}", code);
        ModuleFIE module = moduleFIERepository.findByCode(code)
                .orElseThrow(() -> new NotFoundException("Module not found with code: " + code));
        return convertToModuleFIEDTO(module);
    }
    
    /**
     * Récupérer tous les modules évaluables
     */
    public List<ModuleFIEDTO> getAllEvaluableModules() {
        log.info("Fetching all evaluable modules");
        return moduleFIERepository.findByEvaluableTrue()
                .stream()
                .map(this::convertToModuleFIEDTOSimple)
                .collect(Collectors.toList());
    }
    
    /**
     * Récupérer tous les modules d'un semestre
     */
    public List<ModuleFIEDTO> getModulesBySemestre(Semestre semestre) {
        log.info("Fetching modules for semestre: {}", semestre);
        return moduleFIERepository.findBySemestreAndEvaluableTrue(semestre)
                .stream()
                .map(this::convertToModuleFIEDTOSimple)
                .collect(Collectors.toList());
    }
    
    /**
     * Récupérer les modules d'une UE
     */
    public List<ModuleFIEDTO> getModulesByUniteEnseignement(Long ueId) {
        log.info("Fetching modules for UE: {}", ueId);
        return moduleFIERepository.findByUniteEnseignementId(ueId)
                .stream()
                .map(this::convertToModuleFIEDTOSimple)
                .collect(Collectors.toList());
    }
    
    /**
     * Récupérer une compétence par ID
     */
    public CompetenceDTO getCompetence(Long id) {
        log.info("Fetching competence with ID: {}", id);
        Competence competence = competenceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Competence not found with id: " + id));
        return convertToCompetenceDTO(competence);
    }
    
    /**
     * Récupérer les compétences d'un module (ordonnées par numéro du syllabus)
     */
    public List<CompetenceDTO> getCompetencesByModule(Long moduleId) {
        log.info("Fetching competences for module: {}", moduleId);
        return competenceRepository.findByModuleIdOrderedByNumero(moduleId)
                .stream()
                .map(this::convertToCompetenceDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupérer toutes les compétences (42 du syllabus FIE3)
     */
    public List<CompetenceDTO> getAllCompetences() {
        log.info("Fetching all competences");
        return competenceRepository.findAll()
                .stream()
                .sorted((c1, c2) -> c1.getNumeroOrdre().compareTo(c2.getNumeroOrdre()))
                .map(this::convertToCompetenceDTO)
                .collect(Collectors.toList());
    }
    
    // ============================================================================
    // MAPPERS / CONVERTERS
    // ============================================================================
    
    private UniteEnseignementDTO convertToUniteEnseignementDTO(UniteEnseignement ue) {
        return UniteEnseignementDTO.builder()
                .id(ue.getId())
                .code(ue.getCode())
                .libelle(ue.getLibelle())
                .ects(ue.getEcts())
                .semestre(ue.getSemestre())
                .build();
    }
    
    private ModuleFIEDTO convertToModuleFIEDTO(ModuleFIE module) {
        return ModuleFIEDTO.builder()
                .id(module.getId())
                .code(module.getCode())
                .nom(module.getNom())
                .semestre(module.getSemestre())
                .uniteEnseignement(convertToUniteEnseignementDTO(module.getUniteEnseignement()))
                .heuresCM(module.getHeuresCM())
                .heuresTD(module.getHeuresTD())
                .heuresTP(module.getHeuresTP())
                .heuresProjet(module.getHeuresProjet())
                .heuresTotal(module.getHeuresTotal())
                .evaluable(module.isEvaluable())
                .prerequisTexte(module.getPrerequisTexte())
                .competences(module.getCompetences().stream()
                        .sorted((a, b) -> a.getNumeroOrdre().compareTo(b.getNumeroOrdre()))
                        .map(this::convertToCompetenceDTO)
                        .collect(Collectors.toList()))
                .modulesPrerequisList(module.getModulesPrerequisList().stream()
                        .map(this::convertToModuleFIESimpleDTO)
                        .collect(Collectors.toList()))
                .build();
    }
    
    private ModuleFIEDTO convertToModuleFIEDTOSimple(ModuleFIE module) {
        return ModuleFIEDTO.builder()
                .id(module.getId())
                .code(module.getCode())
                .nom(module.getNom())
                .semestre(module.getSemestre())
                .uniteEnseignement(convertToUniteEnseignementDTO(module.getUniteEnseignement()))
                .heuresCM(module.getHeuresCM())
                .heuresTD(module.getHeuresTD())
                .heuresTP(module.getHeuresTP())
                .heuresProjet(module.getHeuresProjet())
                .heuresTotal(module.getHeuresTotal())
                .evaluable(module.isEvaluable())
                .prerequisTexte(module.getPrerequisTexte())
                .build();
    }
    
    private ModuleFIESimpleDTO convertToModuleFIESimpleDTO(ModuleFIE module) {
        return ModuleFIESimpleDTO.builder()
                .id(module.getId())
                .code(module.getCode())
                .nom(module.getNom())
                .build();
    }
    
    private CompetenceDTO convertToCompetenceDTO(Competence competence) {
        return CompetenceDTO.builder()
                .id(competence.getId())
                .intitule(competence.getIntitule())
                .description(competence.getDescription())
                .numeroOrdre(competence.getNumeroOrdre())
                .niveauAttendu(competence.getNiveauAttendu())
                .poids(competence.getPoids())
                .build();
    }
}
