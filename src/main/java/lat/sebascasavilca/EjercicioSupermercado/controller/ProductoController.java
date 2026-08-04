package lat.sebascasavilca.EjercicioSupermercado.controller;

import lat.sebascasavilca.EjercicioSupermercado.dto.ProductoDto;
import lat.sebascasavilca.EjercicioSupermercado.service.IProductoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    @Autowired
    private IProductoService productoService;

    @GetMapping
    public ResponseEntity<List<ProductoDto>> traerProductos(){
        return ResponseEntity.ok(productoService.traerProductos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoDto> traerProductoPorId(@PathVariable Long id){
        return ResponseEntity.ok(productoService.traerProductoPorId(id));
    }

    @PostMapping
    public ResponseEntity<ProductoDto> crearProducto(@Valid @RequestBody ProductoDto dto){
        ProductoDto creado = productoService.crearProducto(dto);
        return ResponseEntity.created(URI.create("/api/productos" +creado.getId())).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoDto> actualizarProducto(@PathVariable Long id, @Valid @RequestBody ProductoDto dto){
        return ResponseEntity.ok(productoService.actualizarProducto(id,dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id ){
        productoService.eliminarProducto(id);
        return ResponseEntity.noContent().build();
    }
}
