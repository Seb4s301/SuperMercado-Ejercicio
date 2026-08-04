package lat.sebascasavilca.EjercicioSupermercado.controller;

import lat.sebascasavilca.EjercicioSupermercado.dto.VentaDto;
import lat.sebascasavilca.EjercicioSupermercado.service.IVentaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/ventas")
public class VentaController {

    @Autowired
    private IVentaService ventaService;

    @GetMapping
    public ResponseEntity<List<VentaDto>> traerVentas(){
        return ResponseEntity.ok(ventaService.traerVentas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VentaDto> traerVentaPorId(@PathVariable Long id){
        return ResponseEntity.ok(ventaService.traerVentaPorId(id));
    }

    @PostMapping
    public ResponseEntity<VentaDto> create(@Valid @RequestBody VentaDto dto){
        VentaDto created = ventaService.crearVenta(dto);
        return ResponseEntity.created(URI.create("/api/ventas/"+created.getId())).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VentaDto> actualizar(@PathVariable Long id, @Valid @RequestBody VentaDto dto){
        return ResponseEntity.ok(ventaService.actualizarVenta(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        ventaService.eliminarVenta(id);
        return ResponseEntity.noContent().build();
    }
}
