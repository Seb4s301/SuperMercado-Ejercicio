package lat.sebascasavilca.EjercicioSupermercado.config;

import lat.sebascasavilca.EjercicioSupermercado.model.Usuario;
import lat.sebascasavilca.EjercicioSupermercado.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (usuarioRepository.count() == 0) {
            usuarioRepository.save(Usuario.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .rol("ADMIN")
                    .build());

            usuarioRepository.save(Usuario.builder()
                    .username("visual")
                    .password(passwordEncoder.encode("visual123"))
                    .rol("VISUALIZADOR")
                    .build());

            System.out.println("=== Usuarios creados: admin/admin123 (ADMIN), visual/visual123 (VISUALIZADOR) ===");
        }
        System.out.println("=== DataInitializer: " + usuarioRepository.count() + " usuarios en BD ===");
    }
}
