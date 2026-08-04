package lat.sebascasavilca.EjercicioSupermercado.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lat.sebascasavilca.EjercicioSupermercado.dto.LoginRequest;
import lat.sebascasavilca.EjercicioSupermercado.security.CustomUserDetailsService;
import lat.sebascasavilca.EjercicioSupermercado.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    @MockitoBean
    private JwtUtil jwtUtil;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void login_credencialesCorrectas_retorna200ConToken() throws Exception {
        LoginRequest request = new LoginRequest("admin", "admin123");

        UserDetails userDetails = new User("admin", "encoded",
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));

        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(auth);
        when(userDetailsService.estaBloqueado("admin")).thenReturn(false);
        when(jwtUtil.generateToken("admin", "ADMIN")).thenReturn("jwt-token-falso");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token-falso"))
                .andExpect(jsonPath("$.rol").value("ADMIN"))
                .andExpect(jsonPath("$.username").value("admin"));
    }

    @Test
    void login_credencialesIncorrectas_retorna401() throws Exception {
        LoginRequest request = new LoginRequest("admin", "wrong");

        when(userDetailsService.estaBloqueado("admin")).thenReturn(false);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Credenciales incorrectas"));
    }

    @Test
    void login_usuarioBloqueado_retorna429() throws Exception {
        LoginRequest request = new LoginRequest("admin", "admin123");

        when(userDetailsService.estaBloqueado("admin")).thenReturn(true);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isTooManyRequests())
                .andExpect(content().string("Demasiados intentos fallidos. Intenta de nuevo más tarde."));
    }

    @Test
    void login_bodyVacio_retorna401() throws Exception {
        String json = "{}";

        when(userDetailsService.estaBloqueado(null)).thenReturn(false);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_usuarioVisual_retorna200ConRolVisual() throws Exception {
        LoginRequest request = new LoginRequest("visual", "visual123");

        UserDetails userDetails = new User("visual", "encoded",
                List.of(new SimpleGrantedAuthority("ROLE_VISUALIZADOR")));

        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(auth);
        when(userDetailsService.estaBloqueado("visual")).thenReturn(false);
        when(jwtUtil.generateToken("visual", "VISUALIZADOR")).thenReturn("jwt-token-visual");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rol").value("VISUALIZADOR"))
                .andExpect(jsonPath("$.username").value("visual"));
    }
}
