package lat.sebascasavilca.EjercicioSupermercado.controller;

import lat.sebascasavilca.EjercicioSupermercado.dto.DashboardDto;
import lat.sebascasavilca.EjercicioSupermercado.service.IDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private IDashboardService dashboardService;

    @GetMapping
    public ResponseEntity<DashboardDto> obtenerEstadisticas() {
        return ResponseEntity.ok(dashboardService.obtenerEstadisticas());
    }
}
