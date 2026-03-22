package com.example.demo.auth.api;

import com.example.demo.auth.api.dto.UtilisateurResponse;
import com.example.demo.auth.application.GetAllUsersUseCase;
import com.example.demo.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final GetAllUsersUseCase getAllUsersUseCase;

    @GetMapping
    public ResponseEntity<ApiResponse<List<UtilisateurResponse>>> getAllUsers() {
        List<UtilisateurResponse> users = getAllUsersUseCase.execute();
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK.value(),
                "Liste de tous les utilisateurs",
                users
        ));
    }
}
