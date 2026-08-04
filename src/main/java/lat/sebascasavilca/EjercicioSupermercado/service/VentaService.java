package lat.sebascasavilca.EjercicioSupermercado.service;

import lat.sebascasavilca.EjercicioSupermercado.dto.DetalleVentaDto;
import lat.sebascasavilca.EjercicioSupermercado.dto.VentaDto;
import lat.sebascasavilca.EjercicioSupermercado.exception.NotFoundException;
import lat.sebascasavilca.EjercicioSupermercado.mapper.Mapper;
import lat.sebascasavilca.EjercicioSupermercado.model.DetalleVenta;
import lat.sebascasavilca.EjercicioSupermercado.model.Producto;
import lat.sebascasavilca.EjercicioSupermercado.model.Sucursal;
import lat.sebascasavilca.EjercicioSupermercado.model.Venta;
import lat.sebascasavilca.EjercicioSupermercado.repository.ProductoRepository;
import lat.sebascasavilca.EjercicioSupermercado.repository.SucursalRepository;
import lat.sebascasavilca.EjercicioSupermercado.repository.VentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class VentaService implements IVentaService{

    @Autowired
    private VentaRepository ventaRepo;

    @Autowired
    private ProductoRepository productoRepo;

    @Autowired
    private SucursalRepository sucursalRepo;

    @Override
    @Transactional
    public List<VentaDto> traerVentas() {

        List<Venta> ventas = ventaRepo.findAllWithDetails();
        List<VentaDto> ventasDto = new ArrayList<>();

        VentaDto dto;
        for(Venta v : ventas){
            dto = Mapper.toDTO(v);
            ventasDto.add(dto);
        }

        return ventasDto;
    }

    @Override
    @Transactional
    public VentaDto traerVentaPorId(Long id) {
        Venta v = ventaRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Venta no encontrada"));
        return Mapper.toDTO(v);
    }

    @Override
    @Transactional
    public VentaDto crearVenta(VentaDto ventaDto) {
        //Validaciones
        if(ventaDto == null) throw new NotFoundException("VentaDTO es null");
        if(ventaDto.getIdSucursal() == null) throw new NotFoundException("Debe indicar la sucursar");
        if(ventaDto.getDetalle() == null || ventaDto.getDetalle().isEmpty()) throw new NotFoundException("Debe incluir al menos un producto");

        //buscar sucursal
        Sucursal suc = sucursalRepo.findById(ventaDto.getIdSucursal()).orElse(null);
        if (suc == null)throw new NotFoundException("Sucursal no encontrada");

        //crear la venta
        Venta vent = new Venta();
        vent.setFecha(ventaDto.getFecha());
        vent.setEstado(ventaDto.getEstado());
        vent.setSucursal(suc);
        vent.setTotal(ventaDto.getTotal());

        //Lista de detalles
        //-> aqui los productos
        List<DetalleVenta> detalles = new ArrayList<>();

        for(DetalleVentaDto detDTO : ventaDto.getDetalle()){
            Producto p = productoRepo.findByNombre(detDTO.getNombreProd()).orElse(null);
            if(p == null) throw new NotFoundException("Producto no encontrado: " + detDTO.getNombreProd());

            //crear el detalle
            DetalleVenta detalleVent = new DetalleVenta();

                detalleVent.setProd(p);
                detalleVent.setPrecio(detDTO.getPrecio());
                detalleVent.setCantProd(detDTO.getCantProd());
                detalleVent.setVenta(vent);

            detalles.add(detalleVent);
        }

        vent.setDetalle(detalles);

        //calcular total
        Double total = detalles.stream()
                .mapToDouble(d -> d.getPrecio() * d.getCantProd())
                .sum();
        vent.setTotal(total);

        //guardanemos en la BD
        ventaRepo.save(vent);

        //Mapeo de salida
        VentaDto ventaSalida = Mapper.toDTO(vent);
        return ventaSalida;
    }

    @Override
    public VentaDto actualizarVenta(Long id, VentaDto ventaDto) {
        Venta v = ventaRepo.findById(id).orElse(null);
        if(v == null) throw new NotFoundException("Venta no encontrada");

        if(ventaDto.getFecha()!=null) v.setFecha(ventaDto.getFecha());
        if(ventaDto.getEstado()!=null) v.setEstado(ventaDto.getEstado());

        if(ventaDto.getTotal()!= null) v.setTotal(ventaDto.getTotal());
        if(ventaDto.getIdSucursal()!= null){
            Sucursal suc = sucursalRepo.findById(ventaDto.getIdSucursal()).orElse(null);
            if(suc == null) throw new NotFoundException("Sucursal no encontrada");
            v.setSucursal(suc);
        }

        ventaRepo.save(v);

        //pasamos a formato DTO
        VentaDto ventaSalida = Mapper.toDTO(v);

        return ventaSalida;
    }

    @Override
    public void eliminarVenta(Long id) {
        Venta v = ventaRepo.findById(id).orElse(null);
        if(v == null) throw new NotFoundException("Venta no encontrada");
        ventaRepo.delete(v);
    }
}
