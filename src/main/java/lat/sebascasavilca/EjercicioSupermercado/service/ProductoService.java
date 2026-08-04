package lat.sebascasavilca.EjercicioSupermercado.service;

import lat.sebascasavilca.EjercicioSupermercado.dto.ProductoDto;
import lat.sebascasavilca.EjercicioSupermercado.exception.NotFoundException;
import lat.sebascasavilca.EjercicioSupermercado.mapper.Mapper;
import lat.sebascasavilca.EjercicioSupermercado.model.Producto;
import lat.sebascasavilca.EjercicioSupermercado.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoService implements IProductoService{

    @Autowired
    private ProductoRepository repo;

    @Override
    public List<ProductoDto> traerProductos() {
        return repo.findAll().stream().map(Mapper::toDTO).toList();
    }

    @Override
    public ProductoDto traerProductoPorId(Long id) {
        Producto prod = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Producto no encontrado"));
        return Mapper.toDTO(prod);
    }

    @Override
    public ProductoDto crearProducto(ProductoDto productoDto) {
        var prod = Producto.builder()
                .nombre(productoDto.getNombre())
                .categoria(productoDto.getCategoria())
                .precio(productoDto.getPrecio())
                .cantidad(productoDto.getCantidad())
                .build();
        return Mapper.toDTO(repo.save(prod));
    }

    @Override
    public ProductoDto actualizarProducto(Long id, ProductoDto productoDto) {
        //buscamos si existe el producto
        Producto prod = repo.findById(id)
        .orElseThrow(() -> new NotFoundException("Producto no encontrado"));

        prod.setNombre(productoDto.getNombre());
        prod.setCategoria(productoDto.getCategoria());
        prod.setCantidad(productoDto.getCantidad());
        prod.setPrecio(productoDto.getPrecio());

        return Mapper.toDTO(repo.save(prod));
    }

    @Override
    public void eliminarProducto(Long id) {
        if(!repo.existsById(id)){
            throw new NotFoundException("Producto no encontrado para eliminar");
        }
        repo.deleteById(id);
    }
}
