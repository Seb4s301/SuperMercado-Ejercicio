package lat.sebascasavilca.EjercicioSupermercado.security;

import lat.sebascasavilca.EjercicioSupermercado.model.Usuario;
import lat.sebascasavilca.EjercicioSupermercado.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    private static final int MAX_INTENTOS = 5;
    private final Map<String, Integer> intentosFallidos = new ConcurrentHashMap<>();

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        return new User(
                usuario.getUsername(),
                usuario.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRol()))
        );
    }

    public boolean estaBloqueado(String username) {
        Integer intentos = intentosFallidos.getOrDefault(username, 0);
        return intentos >= MAX_INTENTOS;
    }

    public void registrarIntentoFallido(String username) {
        intentosFallidos.merge(username, 1, Integer::sum);
    }

    public void limpiarIntentos(String username) {
        intentosFallidos.remove(username);
    }
}
