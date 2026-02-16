package com.example.demo;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Controller
@Tag(name = "Home", description = "Endpoints de bienvenue et test")
public class HomeController {

    @GetMapping("/")
    @ResponseBody
    @Operation(summary = "Page d'accueil", description = "Retourne un message de bienvenue")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bienvenue affiché avec succès")
    })
    public String home() {
        return "Welcome to ISISU Platform! ✅";
    }

    @GetMapping("/api/hello")
    @ResponseBody
    @Operation(summary = "Endpoint test", description = "Retourne un message de test depuis l'API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Message reçu avec succès")
    })
    public String hello() {
        return "Hello from Spring Boot API!";
    }
}
