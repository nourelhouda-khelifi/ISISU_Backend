package com.example.demo.evaluation.service;

import com.example.demo.referentiel.domain.ModuleFIE;
import com.example.demo.referentiel.infrastructure.ModuleFIERepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * ModuleOrderService — Détermine l'ordre des 11 modules pour une session
 * 
 * Utilise tri topologique pour garantir que les dépendances sont respectées:
 * - Niveau 0 (Foundation): POO, Gestion, Systèmes, BDD, IA (pas dépendances)
 * - Niveau 1 (Intermediate): GL, Épid, CCU, Imagerie (dépendent de Niveau 0)
 * - Niveau 2 (Advanced): WebTech, DevOps (dépendent de Niveau 1)
 * 
 * Dans chaque niveau, l'ordre est ALÉATOIRE pour:
 * - Éviter le biais de toujours mettre POO en premier
 * - Offrir une expérience varier aux étudiants
 */
@Service
@RequiredArgsConstructor
public class ModuleOrderService {
    
    private final ModuleFIERepository moduleFieRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * Calculer l'ordre des 11 modules (ordre aléatoire par niveau)
     * 
     * @return JSON string contenant l'ordre des modules
     */
    public String calculateModuleOrder() {
        // 1. Récupérer tous les modules
        List<ModuleFIE> allModules = moduleFieRepository.findAll();
        
        if (allModules.size() != 11) {
            throw new IllegalStateException(
                "Devrait avoir 11 modules, trouvé: " + allModules.size()
            );
        }
        
        // 2. Grouper par niveau
        Map<Integer, List<ModuleFIE>> byNiveau = allModules.stream()
            .collect(Collectors.groupingBy(ModuleFIE::getNiveau));
        
        // 3. Construire l'ordre: Niveau 0 → Niveau 1 → Niveau 2
        List<ModuleFIE> ordredModules = new ArrayList<>();
        for (int niveau = 0; niveau <= 2; niveau++) {
            List<ModuleFIE> modulesAtNiveau = byNiveau.getOrDefault(niveau, new ArrayList<>());
            
            // Shuffle dans chaque niveau (ordre aléatoire)
            Collections.shuffle(modulesAtNiveau);
            ordredModules.addAll(modulesAtNiveau);
        }
        
        // 4. Convertir en JSON
        return convertToJSON(ordredModules);
    }
    
    /**
     * Convertir liste de modules en JSON
     * 
     * Format simplifié:
     * {
     *   "modules": [
     *     {"code": "E3-1-IN-1", "nom": "POO Java", "niveau": 0},
     *     {"code": "E3-1-IN-3", "nom": "Gestion Projet", "niveau": 0},
     *     ...
     *   ]
     * }
     */
    private String convertToJSON(List<ModuleFIE> modules) {
        try {
            Map<String, Object> data = new HashMap<>();
            List<Map<String, Object>> modulesData = modules.stream()
                .map(m -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("code", m.getCode());
                    map.put("nom", m.getNom());
                    map.put("niveau", m.getNiveau());
                    map.put("ordreNiveau", m.getOrdreNiveau());
                    return map;
                })
                .collect(Collectors.toList());
            
            data.put("modules", modulesData);
            data.put("timestamp", System.currentTimeMillis());
            
            return objectMapper.writeValueAsString(data);
        } catch (Exception e) {
            throw new RuntimeException("Erreur conversion JSON ordre modules", e);
        }
    }
    
    /**
     * Parser le JSON pour récupérer liste de codes modules
     */
    public List<String> parseModuleOrder(String ordreJSON) {
        try {
            Map<?, ?> data = objectMapper.readValue(ordreJSON, Map.class);
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> modules = (List<Map<String, Object>>) data.get("modules");
            
            return modules.stream()
                .map(m -> (String) m.get("code"))
                .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Erreur parsing JSON ordre modules", e);
        }
    }
    
    /**
     * Tri topologique pour vérifier l'ordre
     * (utilisé pour validation/debug)
     */
    public List<ModuleFIE> topologicalSort(List<ModuleFIE> modules) {
        // Construire graphe des dépendances
        Map<ModuleFIE, List<ModuleFIE>> graph = new HashMap<>();
        Map<ModuleFIE, Integer> inDegree = new HashMap<>();
        
        for (ModuleFIE m : modules) {
            graph.put(m, new ArrayList<>(m.getModulesPrerequisList()));
            inDegree.put(m, m.getModulesPrerequisList().size());
        }
        
        // Khan's algorithm
        Queue<ModuleFIE> queue = new LinkedList<>();
        for (ModuleFIE m : modules) {
            if (inDegree.get(m) == 0) {
                queue.offer(m);
            }
        }
        
        List<ModuleFIE> sorted = new ArrayList<>();
        while (!queue.isEmpty()) {
            ModuleFIE current = queue.poll();
            sorted.add(current);
            
            for (ModuleFIE neighbor : graph.getOrDefault(current, new ArrayList<>())) {
                inDegree.put(neighbor, inDegree.get(neighbor) - 1);
                if (inDegree.get(neighbor) == 0) {
                    queue.offer(neighbor);
                }
            }
        }
        
        if (sorted.size() != modules.size()) {
            throw new IllegalStateException("Cycle détecté dans les dépendances!");
        }
        
        return sorted;
    }
}
