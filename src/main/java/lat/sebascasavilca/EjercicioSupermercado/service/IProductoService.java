package lat.sebascasavilca.EjercicioSupermercado.service;

import lat.sebascasavilca.EjercicioSupermercado.dto.ProductoDto;

import java.util.List;

public interface IProductoService {
    List<ProductoDto> traerProductos();
    ProductoDto traerProductoPorId(Long id);
    ProductoDto crearProducto(ProductoDto productoDto);
    ProductoDto actualizarProducto(Long id, ProductoDto productoDto);
    void eliminarProducto(Long id);
}
