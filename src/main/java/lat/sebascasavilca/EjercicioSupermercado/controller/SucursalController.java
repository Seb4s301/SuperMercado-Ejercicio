package lat.sebascasavilca.EjercicioSupermercado.controller;

import lat.sebascasavilca.EjercicioSupermercado.dto.SucursalDto;
import lat.sebascasavilca.EjercicioSupermercado.service.ISucursalService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/sucursales")
public class SucursalController {

    @Autowired
    private ISucursalService sucursalService;

    @GetMapping
    public ResponseEntity<List<SucursalDto>> traerSucursales(){
        return ResponseEntity.ok(sucursalService.traerSucursales());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SucursalDto> traerSucursalPorId(@PathVariable Long id){
        return ResponseEntity.ok(sucursalService.traerSucursalPorId(id));
    }

    @PostMapping
    public ResponseEntity<SucursalDto> create(@Valid @RequestBody SucursalDto dto){
        SucursalDto created = sucursalService.crearSucursal(dto);
        return ResponseEntity.created(URI.create("/api/sucursales/"+created.getId())).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SucursalDto> update(@PathVariable Long id, @Valid @RequestBody SucursalDto dto){
        return ResponseEntity.ok(sucursalService.actualizarSucursal(id,dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        sucursalService.eliminarSucursal(id);
        return ResponseEntity.noContent().build();
    }
}
