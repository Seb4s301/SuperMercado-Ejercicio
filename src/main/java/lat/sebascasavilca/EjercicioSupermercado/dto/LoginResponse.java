package lat.sebascasavilca.EjercicioSupermercado.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {
    private String token;
    private String rol;
    private String username;
}
