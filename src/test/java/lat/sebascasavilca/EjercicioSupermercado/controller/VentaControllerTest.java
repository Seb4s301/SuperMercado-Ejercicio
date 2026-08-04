package lat.sebascasavilca.EjercicioSupermercado.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lat.sebascasavilca.EjercicioSupermercado.dto.VentaDto;
import lat.sebascasavilca.EjercicioSupermercado.exception.NotFoundException;
import lat.sebascasavilca.EjercicioSupermercado.service.IVentaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class VentaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IVentaService ventaService;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Test
    void traerVentas_listaVacia() throws Exception {
        when(ventaService.traerVentas()).thenReturn(List.of());

        mockMvc.perform(get("/api/ventas"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void traerVentaPorId_existe_retorna200() throws Exception {
        VentaDto dto = VentaDto.builder()
                .id(1L)
                .fecha(LocalDate.of(2026, 1, 15))
                .estado("pagada")
                .total(22000.0)
                .idSucursal(1L)
                .build();
        when(ventaService.traerVentaPorId(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/ventas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.estado").value("pagada"))
                .andExpect(jsonPath("$.total").value(22000.0));
    }

    @Test
    void traerVentaPorId_noExiste_retorna404() throws Exception {
        when(ventaService.traerVentaPorId(999L))
                .thenThrow(new NotFoundException("Venta no encontrada"));

        mockMvc.perform(get("/api/ventas/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("Venta no encontrada"));
    }

    @Test
    void crearVenta_sinFecha_retorna400() throws Exception {
        String json = "{\"estado\":\"pendiente\",\"idSucursal\":1}";

        mockMvc.perform(post("/api/ventas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fecha").value("La fecha es obligatoria"));
    }

    @Test
    void crearVenta_sinEstado_retorna400() throws Exception {
        String json = "{\"fecha\":\"2026-01-01\",\"idSucursal\":1}";

        mockMvc.perform(post("/api/ventas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.estado").value("El estado es obligatorio"));
    }

    @Test
    void crearVenta_sinSucursal_retorna400() throws Exception {
        String json = "{\"fecha\":\"2026-01-01\",\"estado\":\"pendiente\"}";

        mockMvc.perform(post("/api/ventas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.idSucursal").value("Debe indicar la sucursal"));
    }

    @Test
    void crearVenta_bodyVacio_retorna400() throws Exception {
        mockMvc.perform(post("/api/ventas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fecha").exists())
                .andExpect(jsonPath("$.estado").exists())
                .andExpect(jsonPath("$.idSucursal").exists());
    }

    @Test
    void crearVenta_sucursalInexistente_retorna404() throws Exception {
        String json = "{\"fecha\":\"2026-01-01\",\"estado\":\"pendiente\",\"idSucursal\":999,"
                + "\"detalle\":[{\"nombreProd\":\"Leche\",\"cantProd\":1,\"precio\":1500}]}";
        when(ventaService.crearVenta(any(VentaDto.class)))
                .thenThrow(new NotFoundException("Sucursal no encontrada"));

        mockMvc.perform(post("/api/ventas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("Sucursal no encontrada"));
    }

    @Test
    void actualizarVenta_noExiste_retorna404() throws Exception {
        when(ventaService.actualizarVenta(eq(999L), any(VentaDto.class)))
                .thenThrow(new NotFoundException("Venta no encontrada"));

        VentaDto input = VentaDto.builder()
                .fecha(LocalDate.of(2026, 1, 1))
                .estado("pagada")
                .idSucursal(1L)
                .build();

        mockMvc.perform(put("/api/ventas/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("Venta no encontrada"));
    }

    @Test
    void eliminarVenta_noExiste_retorna404() throws Exception {
        org.mockito.Mockito.doThrow(new NotFoundException("Venta no encontrada"))
                .when(ventaService).eliminarVenta(999L);

        mockMvc.perform(delete("/api/ventas/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("Venta no encontrada"));
    }

    @Test
    void eliminarVenta_existe_retorna204() throws Exception {
        mockMvc.perform(delete("/api/ventas/1"))
                .andExpect(status().isNoContent());
    }
}
