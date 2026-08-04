package lat.sebascasavilca.EjercicioSupermercado.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lat.sebascasavilca.EjercicioSupermercado.dto.SucursalDto;
import lat.sebascasavilca.EjercicioSupermercado.exception.NotFoundException;
import lat.sebascasavilca.EjercicioSupermercado.service.ISucursalService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class SucursalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ISucursalService sucursalService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void traerSucursales_listaVacia() throws Exception {
        when(sucursalService.traerSucursales()).thenReturn(List.of());

        mockMvc.perform(get("/api/sucursales"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void traerSucursales_conDatos() throws Exception {
        SucursalDto dto = SucursalDto.builder()
                .id(1L).nombre("Centro").direccion("Av. Principal 123").build();
        when(sucursalService.traerSucursales()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/sucursales"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Centro"))
                .andExpect(jsonPath("$[0].direccion").value("Av. Principal 123"));
    }

    @Test
    void traerSucursalPorId_existe_retorna200() throws Exception {
        SucursalDto dto = SucursalDto.builder()
                .id(1L).nombre("Centro").direccion("Av. Principal 123").build();
        when(sucursalService.traerSucursalPorId(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/sucursales/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Centro"));
    }

    @Test
    void traerSucursalPorId_noExiste_retorna404() throws Exception {
        when(sucursalService.traerSucursalPorId(999L))
                .thenThrow(new NotFoundException("Sucursal no encontrada"));

        mockMvc.perform(get("/api/sucursales/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("Sucursal no encontrada"));
    }

    @Test
    void crearSucursal_valido_retorna201() throws Exception {
        SucursalDto input = SucursalDto.builder()
                .nombre("Centro").direccion("Av. Principal 123").build();
        SucursalDto output = SucursalDto.builder()
                .id(1L).nombre("Centro").direccion("Av. Principal 123").build();
        when(sucursalService.crearSucursal(any(SucursalDto.class))).thenReturn(output);

        mockMvc.perform(post("/api/sucursales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Centro"));
    }

    @Test
    void crearSucursal_nombreVacio_retorna400() throws Exception {
        SucursalDto input = SucursalDto.builder()
                .nombre("").direccion("Av. Principal 123").build();

        mockMvc.perform(post("/api/sucursales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.nombre").value("El nombre es obligatorio"));
    }

    @Test
    void crearSucursal_direccionVacia_retorna400() throws Exception {
        SucursalDto input = SucursalDto.builder()
                .nombre("Centro").direccion("").build();

        mockMvc.perform(post("/api/sucursales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.direccion").value("La dirección es obligatoria"));
    }

    @Test
    void crearSucursal_camposFaltantes_retorna400() throws Exception {
        String json = "{}";

        mockMvc.perform(post("/api/sucursales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.nombre").exists())
                .andExpect(jsonPath("$.direccion").exists());
    }

    @Test
    void actualizarSucursal_noExiste_retorna404() throws Exception {
        when(sucursalService.actualizarSucursal(eq(999L), any(SucursalDto.class)))
                .thenThrow(new NotFoundException("Sucursal no encontrada"));

        SucursalDto input = SucursalDto.builder()
                .nombre("Centro").direccion("Av. Principal 123").build();

        mockMvc.perform(put("/api/sucursales/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("Sucursal no encontrada"));
    }

    @Test
    void actualizarSucursal_nombreVacio_retorna400() throws Exception {
        SucursalDto input = SucursalDto.builder()
                .nombre("").direccion("Av. Principal 123").build();

        mockMvc.perform(put("/api/sucursales/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.nombre").value("El nombre es obligatorio"));
    }

    @Test
    void eliminarSucursal_noExiste_retorna404() throws Exception {
        org.mockito.Mockito.doThrow(new NotFoundException("Sucursal no encontrada para eliminar"))
                .when(sucursalService).eliminarSucursal(999L);

        mockMvc.perform(delete("/api/sucursales/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("Sucursal no encontrada para eliminar"));
    }

    @Test
    void eliminarSucursal_existe_retorna204() throws Exception {
        mockMvc.perform(delete("/api/sucursales/1"))
                .andExpect(status().isNoContent());
    }
}
