package lat.sebascasavilca.EjercicioSupermercado.service;

import lat.sebascasavilca.EjercicioSupermercado.dto.SucursalDto;

import java.util.List;

public interface ISucursalService{

    List<SucursalDto> traerSucursales();
    SucursalDto traerSucursalPorId(Long id);
    SucursalDto crearSucursal(SucursalDto sucursaldto);
    SucursalDto actualizarSucursal(Long id, SucursalDto sucursalDto);
    void eliminarSucursal(Long id);
}
