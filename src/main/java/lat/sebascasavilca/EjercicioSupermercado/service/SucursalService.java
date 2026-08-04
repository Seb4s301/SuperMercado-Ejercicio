package lat.sebascasavilca.EjercicioSupermercado.service;

import lat.sebascasavilca.EjercicioSupermercado.dto.SucursalDto;
import lat.sebascasavilca.EjercicioSupermercado.exception.NotFoundException;
import lat.sebascasavilca.EjercicioSupermercado.mapper.Mapper;
import lat.sebascasavilca.EjercicioSupermercado.model.Sucursal;
import lat.sebascasavilca.EjercicioSupermercado.repository.SucursalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SucursalService implements ISucursalService{

    @Autowired
    private SucursalRepository repo;

    @Override
    public List<SucursalDto> traerSucursales() {
        return repo.findAll()
                .stream()
                .map(Mapper::toDTO)
                .toList();
    }

    @Override
    public SucursalDto traerSucursalPorId(Long id) {
        Sucursal suc = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Sucursal no encontrada"));
        return Mapper.toDTO(suc);
    }

    @Override
    public SucursalDto crearSucursal(SucursalDto sucursaldto) {
        Sucursal suc = Sucursal.builder()
                .nombre(sucursaldto.getNombre())
                .direccion(sucursaldto.getDireccion())
                .build();
        return Mapper.toDTO(repo.save(suc));
    }

    @Override
    public SucursalDto actualizarSucursal(Long id, SucursalDto sucursalDto) {
        //validamos su existencia
        Sucursal suc = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Sucursal no encontrada"));

        suc.setNombre(sucursalDto.getNombre());
        suc.setDireccion(sucursalDto.getDireccion());

        return Mapper.toDTO(repo.save(suc));
    }

    @Override
    public void eliminarSucursal(Long id) {

        if(!repo.existsById(id)){
            throw new NotFoundException("Sucursal no encontrada para eliminar");
        }
        repo.deleteById(id);
    }
}
