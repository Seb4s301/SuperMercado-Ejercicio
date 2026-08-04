package lat.sebascasavilca.EjercicioSupermercado.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lat.sebascasavilca.EjercicioSupermercado.dto.ProductoDto;
import lat.sebascasavilca.EjercicioSupermercado.exception.NotFoundException;
import lat.sebascasavilca.EjercicioSupermercado.service.IProductoService;
import org.junit.jupiter.api.BeforeEach;
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
class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IProductoService productoService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void traerProductos_listaVacia() throws Exception {
        when(productoService.traerProductos()).thenReturn(List.of());

        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void traerProductos_conDatos() throws Exception {
        ProductoDto dto = ProductoDto.builder()
                .id(1L).nombre("Leche").categoria("Lacteos")
                .precio(1500.0).cantidad(50).build();
        when(productoService.traerProductos()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Leche"));
    }

    @Test
    void traerProductoPorId_existe_retorna200() throws Exception {
        ProductoDto dto = ProductoDto.builder()
                .id(1L).nombre("Leche").categoria("Lacteos")
                .precio(1500.0).cantidad(50).build();
        when(productoService.traerProductoPorId(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/productos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Leche"))
                .andExpect(jsonPath("$.precio").value(1500.0));
    }

    @Test
    void traerProductoPorId_noExiste_retorna404() throws Exception {
        when(productoService.traerProductoPorId(999L))
                .thenThrow(new NotFoundException("Producto no encontrado"));

        mockMvc.perform(get("/api/productos/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("Producto no encontrado"));
    }

    @Test
    void crearProducto_valido_retorna201() throws Exception {
        ProductoDto input = ProductoDto.builder()
                .nombre("Leche").categoria("Lacteos")
                .precio(1500.0).cantidad(50).build();
        ProductoDto output = ProductoDto.builder()
                .id(1L).nombre("Leche").categoria("Lacteos")
                .precio(1500.0).cantidad(50).build();
        when(productoService.crearProducto(any(ProductoDto.class))).thenReturn(output);

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Leche"));
    }

    @Test
    void crearProducto_nombreVacio_retorna400() throws Exception {
        ProductoDto input = ProductoDto.builder()
                .nombre("").categoria("Lacteos")
                .precio(1500.0).cantidad(50).build();

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.nombre").value("El nombre es obligatorio"));
    }

    @Test
    void crearProducto_categoriaVacia_retorna400() throws Exception {
        ProductoDto input = ProductoDto.builder()
                .nombre("Leche").categoria("")
                .precio(1500.0).cantidad(50).build();

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.categoria").value("La categoría es obligatoria"));
    }

    @Test
    void crearProducto_precioNegativo_retorna400() throws Exception {
        ProductoDto input = ProductoDto.builder()
                .nombre("Leche").categoria("Lacteos")
                .precio(-100.0).cantidad(50).build();

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.precio").value("El precio no puede ser negativo"));
    }

    @Test
    void crearProducto_cantidadNegativa_retorna400() throws Exception {
        ProductoDto input = ProductoDto.builder()
                .nombre("Leche").categoria("Lacteos")
                .precio(1500.0).cantidad(-1).build();

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.cantidad").value("La cantidad no puede ser negativa"));
    }

    @Test
    void crearProducto_campoFaltante_retorna400() throws Exception {
        String json = "{\"nombre\":\"Leche\",\"precio\":1000,\"cantidad\":5}";

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.categoria").exists());
    }

    @Test
    void actualizarProducto_noExiste_retorna404() throws Exception {
        when(productoService.actualizarProducto(eq(999L), any(ProductoDto.class)))
                .thenThrow(new NotFoundException("Producto no encontrado"));

        ProductoDto input = ProductoDto.builder()
                .nombre("Leche").categoria("Lacteos")
                .precio(1500.0).cantidad(50).build();

        mockMvc.perform(put("/api/productos/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("Producto no encontrado"));
    }

    @Test
    void actualizarProducto_nombreVacio_retorna400() throws Exception {
        ProductoDto input = ProductoDto.builder()
                .nombre("").categoria("Lacteos")
                .precio(1500.0).cantidad(50).build();

        mockMvc.perform(put("/api/productos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.nombre").value("El nombre es obligatorio"));
    }

    @Test
    void eliminarProducto_noExiste_retorna404() throws Exception {
        org.mockito.Mockito.doThrow(new NotFoundException("Producto no encontrado para eliminar"))
                .when(productoService).eliminarProducto(999L);

        mockMvc.perform(delete("/api/productos/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("Producto no encontrado para eliminar"));
    }

    @Test
    void eliminarProducto_existe_retorna204() throws Exception {
        mockMvc.perform(delete("/api/productos/1"))
                .andExpect(status().isNoContent());
    }
}
