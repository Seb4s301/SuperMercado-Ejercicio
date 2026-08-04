package lat.sebascasavilca.EjercicioSupermercado.repository;

import lat.sebascasavilca.EjercicioSupermercado.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    //busqueda de producto por nombre
    Optional<Producto> findByNombre(String nombre);
}
