package lat.sebascasavilca.EjercicioSupermercado.service;

import lat.sebascasavilca.EjercicioSupermercado.dto.VentaDto;

import java.util.List;


public interface IVentaService {

    List<VentaDto> traerVentas();
    VentaDto traerVentaPorId(Long id);
    VentaDto crearVenta(VentaDto ventaDto);
    VentaDto actualizarVenta(Long id, VentaDto ventaDto);
    void eliminarVenta(Long id);
}
