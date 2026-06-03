package mx.com.adrian.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import mx.com.adrian.demo.exception.GlobalExceptionHandler;
import mx.com.adrian.demo.exception.ResourceNotFoundException;
import mx.com.adrian.demo.model.ProductoRequestDTO;
import mx.com.adrian.demo.model.ProductoResponseDTO;
import mx.com.adrian.demo.service.ProductoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ProductoControllerTest {

    private MockMvc mockMvc;
    private ProductoService productoService;
    private ObjectMapper objectMapper;
    private ProductoResponseDTO responseDTO;
    private ProductoRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        productoService = mock(ProductoService.class);
        ProductoController controller = new ProductoController(productoService);
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        responseDTO = ProductoResponseDTO.builder()
                .id(1L)
                .nombre("Laptop")
                .descripcion("Laptop gaming")
                .precio(new BigDecimal("15000.00"))
                .cantidadStock(10)
                .categoria("Electrónica")
                .fechaCreacion(LocalDateTime.now())
                .activo(true)
                .build();

        requestDTO = new ProductoRequestDTO();
        requestDTO.setNombre("Laptop");
        requestDTO.setDescripcion("Laptop gaming");
        requestDTO.setPrecio(new BigDecimal("15000.00"));
        requestDTO.setCantidadStock(10);
        requestDTO.setCategoria("Electrónica");
    }

    @Test
    void crearProducto_Retorna201() throws Exception {
        when(productoService.crearProducto(any(ProductoRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombre", is("Laptop")))
                .andExpect(jsonPath("$.precio", is(15000.00)))
                .andExpect(jsonPath("$.activo", is(true)));
    }

    @Test
    void listarProductos_Retorna200() throws Exception {
        Page<ProductoResponseDTO> page = new PageImpl<>(List.of(responseDTO), PageRequest.of(0, 10), 1);
        when(productoService.listarProductos(any())).thenReturn(page);

        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].nombre", is("Laptop")));
    }

    @Test
    void obtenerProducto_Retorna200() throws Exception {
        when(productoService.obtenerProducto(1L)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/productos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombre", is("Laptop")));
    }

    @Test
    void obtenerProducto_NoExistente_Retorna404() throws Exception {
        when(productoService.obtenerProducto(999L))
                .thenThrow(new ResourceNotFoundException("Producto no encontrado con ID: 999"));

        mockMvc.perform(get("/api/productos/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("no encontrado")));
    }

    @Test
    void actualizarProducto_Retorna200() throws Exception {
        when(productoService.actualizarProducto(eq(1L), any(ProductoRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(put("/api/productos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre", is("Laptop")));
    }

    @Test
    void eliminarProducto_Retorna204() throws Exception {
        doNothing().when(productoService).eliminarProducto(1L);

        mockMvc.perform(delete("/api/productos/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void listarPorCategoria_Retorna200() throws Exception {
        when(productoService.listarPorCategoria("Electrónica")).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/productos/categoria/Electrónica"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].categoria", is("Electrónica")));
    }

    @Test
    void buscarPorRangoPrecio_Retorna200() throws Exception {
        when(productoService.buscarPorRangoPrecio(new BigDecimal("1000"), new BigDecimal("20000")))
                .thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/productos/buscar")
                        .param("precioMin", "1000")
                        .param("precioMax", "20000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void crearProducto_ConDatosInvalidos_Retorna400() throws Exception {
        ProductoRequestDTO invalid = new ProductoRequestDTO();
        invalid.setNombre("AB");
        invalid.setPrecio(new BigDecimal("-1"));
        invalid.setCantidadStock(-5);

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }
}
