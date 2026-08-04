package lat.sebascasavilca.EjercicioSupermercado.controller;

import lat.sebascasavilca.EjercicioSupermercado.dto.LoginRequest;
import lat.sebascasavilca.EjercicioSupermercado.dto.LoginResponse;
import lat.sebascasavilca.EjercicioSupermercado.security.CustomUserDetailsService;
import lat.sebascasavilca.EjercicioSupermercado.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        if (userDetailsService.estaBloqueado(request.getUsername())) {
            return ResponseEntity.status(429).body("Demasiados intentos fallidos. Intenta de nuevo más tarde.");
        }

        try {
            Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            String rol = userDetails.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");

            userDetailsService.limpiarIntentos(request.getUsername());

            String token = jwtUtil.generateToken(userDetails.getUsername(), rol);

            return ResponseEntity.ok(LoginResponse.builder()
                    .token(token)
                    .rol(rol)
                    .username(userDetails.getUsername())
                    .build());

        } catch (Exception e) {
            userDetailsService.registrarIntentoFallido(request.getUsername());
            return ResponseEntity.status(401).body("Credenciales incorrectas");
        }
    }
}
