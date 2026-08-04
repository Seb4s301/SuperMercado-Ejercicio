package lat.sebascasavilca.EjercicioSupermercado.mapper;

import lat.sebascasavilca.EjercicioSupermercado.dto.DetalleVentaDto;
import lat.sebascasavilca.EjercicioSupermercado.dto.ProductoDto;
import lat.sebascasavilca.EjercicioSupermercado.dto.SucursalDto;
import lat.sebascasavilca.EjercicioSupermercado.dto.UsuarioDto;
import lat.sebascasavilca.EjercicioSupermercado.dto.VentaDto;
import lat.sebascasavilca.EjercicioSupermercado.model.Producto;
import lat.sebascasavilca.EjercicioSupermercado.model.Sucursal;
import lat.sebascasavilca.EjercicioSupermercado.model.Usuario;
import lat.sebascasavilca.EjercicioSupermercado.model.Venta;

import java.util.stream.Collectors;

public class Mapper {

    //Mappeo de Producto a ProductoDTO
    public static ProductoDto toDTO(Producto p){
        if(p == null) return null;

        return ProductoDto.builder()
                .id(p.getId())
                .nombre(p.getNombre())
                .categoria(p.getCategoria())
                .precio(p.getPrecio())
                .cantidad(p.getCantidad())
                .build();
    }

    //Mappeo de Venta a VentaDTO
    public static VentaDto toDTO(Venta venta){
        if(venta == null) return null;

        var detalle = venta.getDetalle().stream().map( det->
                DetalleVentaDto.builder()
                        .id(det.getProd().getId())
                        .nombreProd(det.getProd().getNombre())
                        .cantProd(det.getCantProd())
                        .precio(det.getPrecio())
                        .subtotal(det.getPrecio() * det.getCantProd())
                        .build())
                        .collect(Collectors.toList());
        var total =detalle.stream()
                .map(DetalleVentaDto::getSubtotal)
                .reduce(0.0, Double::sum);

        return VentaDto.builder()
                .id(venta.getId())
                .fecha(venta.getFecha())
                .idSucursal(venta.getSucursal().getId())
                .estado(venta.getEstado())
                .detalle(detalle)
                .total(total)
                .build();
    }

    //Mappeo de Sucursal a SucursalDTO
    public static SucursalDto toDTO(Sucursal s){
        if(s == null) return null;
        return SucursalDto.builder()
                .id(s.getId())
                .nombre(s.getNombre())
                .direccion(s.getDireccion())
                .build();
    }

    //Mappeo de Usuario a UsuarioDTO (sin password)
    public static UsuarioDto toDTO(Usuario u){
        if(u == null) return null;
        return UsuarioDto.builder()
                .id(u.getId())
                .username(u.getUsername())
                .rol(u.getRol())
                .build();
    }
}
