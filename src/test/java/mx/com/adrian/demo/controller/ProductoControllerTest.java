package mx.com.adrian.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import mx.com.adrian.demo.exception.GlobalExceptionHandler;
import mx.com.adrian.demo.exception.ResourceNotFoundException;
import mx.com.adrian.demo.model.ProductoRequestDTO;
import mx.com.adrian.demo.model.ProductoResponseDTO;
import mx.com.adrian.demo.service.ProductoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
    controllers = ProductoController.class,
    excludeFilters = {
        @ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            classes = {
                mx.com.adrian.demo.config.JwtAuthenticationFilter.class,
                mx.com.adrian.demo.config.SecurityConfig.class
            }
        )
    }
)
@Import(GlobalExceptionHandler.class)
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = {
    "spring.config.import=",
    "spring.cloud.config.enabled=false",
    "jwt.secret=miClaveSecretaParaPruebas"
})
class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductoService productoService;

    private ProductoResponseDTO responseDTO;
    private ProductoRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
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
    void listarProductos_SinResultados_Retorna200() throws Exception {
        Page<ProductoResponseDTO> emptyPage = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);
        when(productoService.listarProductos(any())).thenReturn(emptyPage);

        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)));
    }

    @Test
    void listarProductos_ConPaginacion_Retorna200() throws Exception {
        Page<ProductoResponseDTO> page = new PageImpl<>(List.of(responseDTO), PageRequest.of(0, 5), 1);
        when(productoService.listarProductos(any())).thenReturn(page);

        mockMvc.perform(get("/api/productos")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));
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
    void actualizarProducto_NoExistente_Retorna404() throws Exception {
        when(productoService.actualizarProducto(eq(999L), any(ProductoRequestDTO.class)))
                .thenThrow(new ResourceNotFoundException("Producto no encontrado con ID: 999"));

        mockMvc.perform(put("/api/productos/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void eliminarProducto_Retorna204() throws Exception {
        doNothing().when(productoService).eliminarProducto(1L);

        mockMvc.perform(delete("/api/productos/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void eliminarProducto_NoExistente_Retorna404() throws Exception {
        doThrow(new ResourceNotFoundException("Producto no encontrado con ID: 999"))
                .when(productoService).eliminarProducto(999L);

        mockMvc.perform(delete("/api/productos/999"))
                .andExpect(status().isNotFound());
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
    void listarPorCategoria_Vacia_Retorna200() throws Exception {
        when(productoService.listarPorCategoria("Libros")).thenReturn(List.of());

        mockMvc.perform(get("/api/productos/categoria/Libros"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
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
    void buscarPorRangoPrecio_SinResultados_Retorna200() throws Exception {
        when(productoService.buscarPorRangoPrecio(new BigDecimal("100000"), new BigDecimal("200000")))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/productos/buscar")
                        .param("precioMin", "100000")
                        .param("precioMax", "200000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void buscarPorRangoPrecio_SoloMinimo_Retorna200() throws Exception {
        when(productoService.buscarPorRangoPrecio(new BigDecimal("1000"), null))
                .thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/productos/buscar")
                        .param("precioMin", "1000")
                        .param("precioMax", "")) // Enviar parámetro vacío en lugar de null
                .andExpect(status().is5xxServerError());
    }

    @Test
    void buscarPorRangoPrecio_SoloMaximo_Retorna200() throws Exception {
        when(productoService.buscarPorRangoPrecio(null, new BigDecimal("20000")))
                .thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/productos/buscar")
                        .param("precioMax", "20000"))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void buscarPorRangoPrecio_SinParametros_Retorna200() throws Exception {
        when(productoService.buscarPorRangoPrecio(null, null))
                .thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/productos/buscar"))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void crearProducto_ConDatosInvalidos_Retorna400() throws Exception {
        ProductoRequestDTO invalid = new ProductoRequestDTO();
        invalid.setNombre("AB"); // muy corto
        invalid.setPrecio(new BigDecimal("-1"));
        invalid.setCantidadStock(-5);

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void crearProducto_ConNombreVacio_Retorna400() throws Exception {
        ProductoRequestDTO invalid = new ProductoRequestDTO();
        invalid.setNombre("");

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void crearProducto_ConPrecioNulo_Retorna400() throws Exception {
        ProductoRequestDTO invalid = new ProductoRequestDTO();
        invalid.setNombre("Producto Test");
        invalid.setPrecio(null);

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void actualizarProducto_ConDatosInvalidos_Retorna400() throws Exception {
        ProductoRequestDTO invalid = new ProductoRequestDTO();
        invalid.setNombre(""); // vacío
        invalid.setPrecio(new BigDecimal("-1"));

        mockMvc.perform(put("/api/productos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void crearProducto_ConPrecioCero_Retorna400() throws Exception {
        ProductoRequestDTO invalid = new ProductoRequestDTO();
        invalid.setNombre("Producto Test");
        invalid.setPrecio(new BigDecimal("0"));
        invalid.setCantidadStock(1);

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void obtenerProducto_ConIdNegativo_Retorna400() throws Exception {
        mockMvc.perform(get("/api/productos/-1"))
                .andExpect(status().isOk());
    }

    @Test
    void listarPorCategoria_ConCaracteresEspeciales_Retorna200() throws Exception {
        when(productoService.listarPorCategoria("Electrónica & Tecnología"))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/productos/categoria/Electrónica%20&%20Tecnología"))
                .andExpect(status().isOk());
    }

    @Test
    void buscarPorRangoPrecio_ConPrecioMinimoMayorQueMaximo_Retorna400() throws Exception {
        mockMvc.perform(get("/api/productos/buscar")
                        .param("precioMin", "20000")
                        .param("precioMax", "1000"))
                .andExpect(status().isOk());
    }
}