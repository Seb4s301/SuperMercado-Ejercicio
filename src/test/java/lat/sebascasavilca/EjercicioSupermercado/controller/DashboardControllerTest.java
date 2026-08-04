package lat.sebascasavilca.EjercicioSupermercado.controller;

import lat.sebascasavilca.EjercicioSupermercado.dto.DashboardDto;
import lat.sebascasavilca.EjercicioSupermercado.service.IDashboardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IDashboardService dashboardService;

    @Test
    void obtenerEstadisticas_conDatos_retorna200() throws Exception {
        DashboardDto dto = DashboardDto.builder()
                .totalProductos(3)
                .totalSucursales(2)
                .totalVentas(5)
                .ingresoTotal(38000.0)
                .ventasPorSucursal(List.of(
                        new DashboardDto.VentasPorSucursal("Centro", 3),
                        new DashboardDto.VentasPorSucursal("Norte", 2)
                ))
                .productosMasVendidos(List.of(
                        new DashboardDto.ProductoVendido("Leche", 50),
                        new DashboardDto.ProductoVendido("Pan", 30)
                ))
                .ventasPorMes(List.of(
                        new DashboardDto.VentasPorMes("2026-01", 3, 22000.0),
                        new DashboardDto.VentasPorMes("2026-02", 2, 16000.0)
                ))
                .build();
        when(dashboardService.obtenerEstadisticas()).thenReturn(dto);

        mockMvc.perform(get("/api/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalProductos").value(3))
                .andExpect(jsonPath("$.totalSucursales").value(2))
                .andExpect(jsonPath("$.totalVentas").value(5))
                .andExpect(jsonPath("$.ingresoTotal").value(38000.0))
                .andExpect(jsonPath("$.ventasPorSucursal.length()").value(2))
                .andExpect(jsonPath("$.ventasPorSucursal[0].nombreSucursal").value("Centro"))
                .andExpect(jsonPath("$.ventasPorSucursal[0].cantidadVentas").value(3))
                .andExpect(jsonPath("$.productosMasVendidos.length()").value(2))
                .andExpect(jsonPath("$.productosMasVendidos[0].nombreProducto").value("Leche"))
                .andExpect(jsonPath("$.ventasPorMes.length()").value(2))
                .andExpect(jsonPath("$.ventasPorMes[0].mes").value("2026-01"));
    }

    @Test
    void obtenerEstadisticas_vacio_retorna200() throws Exception {
        DashboardDto dto = DashboardDto.builder()
                .totalProductos(0)
                .totalSucursales(0)
                .totalVentas(0)
                .ingresoTotal(0.0)
                .ventasPorSucursal(List.of())
                .productosMasVendidos(List.of())
                .ventasPorMes(List.of())
                .build();
        when(dashboardService.obtenerEstadisticas()).thenReturn(dto);

        mockMvc.perform(get("/api/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalProductos").value(0))
                .andExpect(jsonPath("$.totalSucursales").value(0))
                .andExpect(jsonPath("$.totalVentas").value(0));
    }
}
