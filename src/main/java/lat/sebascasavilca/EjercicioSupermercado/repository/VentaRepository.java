package lat.sebascasavilca.EjercicioSupermercado.repository;

import lat.sebascasavilca.EjercicioSupermercado.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VentaRepository extends JpaRepository<Venta, Long> {

    @Query("SELECT v FROM Venta v LEFT JOIN FETCH v.detalle d LEFT JOIN FETCH d.prod LEFT JOIN FETCH v.sucursal")
    List<Venta> findAllWithDetails();
}
