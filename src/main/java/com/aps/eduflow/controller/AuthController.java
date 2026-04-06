package com.aps.eduflow.controller;

import com.aps.eduflow.domain.dto.LoginRequest;
import com.aps.eduflow.domain.dto.LoginResponse;
import com.aps.eduflow.domain.entity.Usuario;
import com.aps.eduflow.domain.repository.UsuarioRepository;
import com.aps.eduflow.infra.security.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest dto) {
        try {
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getSenha());

            authenticationManager.authenticate(authToken);

            Usuario usuario = usuarioRepository.findByEmail(dto.getEmail()).orElseThrow();
            String token = tokenService.generateToken(usuario);

            return ResponseEntity.ok(new LoginResponse(token, usuario.getRole().name(), usuario.getId()));
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Credenciais inválidas");
        }
    }
}
