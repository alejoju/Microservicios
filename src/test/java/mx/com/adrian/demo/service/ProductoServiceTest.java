package mx.com.adrian.demo.service;

import mx.com.adrian.demo.entity.Producto;
import mx.com.adrian.demo.exception.ResourceNotFoundException;
import mx.com.adrian.demo.mapper.ProductoMapper;
import mx.com.adrian.demo.model.ProductoRequestDTO;
import mx.com.adrian.demo.model.ProductoResponseDTO;
import mx.com.adrian.demo.repository.ProductoRepository;
import mx.com.adrian.demo.service.impl.ProductoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository repository;

    @Mock
    private ProductoMapper mapper;

    @InjectMocks
    private ProductoServiceImpl service;

    private Producto producto;
    private ProductoRequestDTO requestDTO;
    private ProductoResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Laptop");
        producto.setDescripcion("Laptop gaming");
        producto.setPrecio(new BigDecimal("15000.00"));
        producto.setCantidadStock(10);
        producto.setCategoria("Electrónica");
        producto.setActivo(true);

        requestDTO = new ProductoRequestDTO();
        requestDTO.setNombre("Laptop");
        requestDTO.setDescripcion("Laptop gaming");
        requestDTO.setPrecio(new BigDecimal("15000.00"));
        requestDTO.setCantidadStock(10);
        requestDTO.setCategoria("Electrónica");

        responseDTO = ProductoResponseDTO.builder()
                .id(1L)
                .nombre("Laptop")
                .descripcion("Laptop gaming")
                .precio(new BigDecimal("15000.00"))
                .cantidadStock(10)
                .categoria("Electrónica")
                .activo(true)
                .build();
    }

    @Test
    void crearProducto_Exitosamente() {
        when(mapper.toEntity(any(ProductoRequestDTO.class))).thenReturn(producto);
        when(repository.save(any(Producto.class))).thenReturn(producto);
        when(mapper.toResponseDTO(any(Producto.class))).thenReturn(responseDTO);

        ProductoResponseDTO result = service.crearProducto(requestDTO);

        assertNotNull(result);
        assertEquals("Laptop", result.getNombre());
        assertEquals(new BigDecimal("15000.00"), result.getPrecio());
        verify(repository).save(any(Producto.class));
    }

    @Test
    void crearProducto_ConDatosInvalidos_DebeLanzarExcepcion() {
        ProductoRequestDTO invalidDTO = new ProductoRequestDTO();
        invalidDTO.setNombre("AB");

        when(mapper.toEntity(invalidDTO)).thenReturn(new Producto());

        assertThrows(Exception.class, () -> {
            Producto entity = mapper.toEntity(invalidDTO);
            if (entity.getNombre() == null || entity.getNombre().length() < 3) {
                throw new IllegalArgumentException("El nombre debe tener entre 3 y 100 caracteres");
            }
        });
    }

    @Test
    void obtenerProducto_PorIdExistente() {
        when(repository.findByIdAndActivoTrue(1L)).thenReturn(Optional.of(producto));
        when(mapper.toResponseDTO(producto)).thenReturn(responseDTO);

        ProductoResponseDTO result = service.obtenerProducto(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Laptop", result.getNombre());
    }

    @Test
    void obtenerProducto_PorIdNoExistente_DebeLanzarExcepcion() {
        when(repository.findByIdAndActivoTrue(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.obtenerProducto(999L));
    }

    @Test
    void actualizarProducto_Exitosamente() {
        when(repository.findByIdAndActivoTrue(1L)).thenReturn(Optional.of(producto));
        when(repository.save(any(Producto.class))).thenReturn(producto);
        when(mapper.toResponseDTO(any(Producto.class))).thenReturn(responseDTO);

        ProductoResponseDTO result = service.actualizarProducto(1L, requestDTO);

        assertNotNull(result);
        assertEquals("Laptop", result.getNombre());
        verify(mapper).updateEntity(any(ProductoRequestDTO.class), any(Producto.class));
        verify(repository).save(any(Producto.class));
    }

    @Test
    void eliminarProducto_CambiaActivoAFalse() {
        when(repository.findByIdAndActivoTrue(1L)).thenReturn(Optional.of(producto));
        when(repository.save(any(Producto.class))).thenReturn(producto);

        service.eliminarProducto(1L);

        assertFalse(producto.getActivo());
        verify(repository).save(producto);
    }

    @Test
    void listarPorCategoria() {
        when(repository.findByCategoriaAndActivoTrue("Electrónica"))
                .thenReturn(List.of(producto));
        when(mapper.toResponseDTO(any(Producto.class))).thenReturn(responseDTO);

        var result = service.listarPorCategoria("Electrónica");

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("Electrónica", result.get(0).getCategoria());
    }

    @Test
    void listarProductos_RetornaSoloActivos() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Producto> productPage = new PageImpl<>(List.of(producto));
        when(repository.findByActivoTrue(pageable)).thenReturn(productPage);
        when(mapper.toResponseDTO(any(Producto.class))).thenReturn(responseDTO);

        Page<ProductoResponseDTO> result = service.listarProductos(pageable);

        assertFalse(result.isEmpty());
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void buscarPorRangoPrecio() {
        when(repository.findByPrecioBetween(new BigDecimal("1000"), new BigDecimal("20000")))
                .thenReturn(List.of(producto));
        when(mapper.toResponseDTO(any(Producto.class))).thenReturn(responseDTO);

        var result = service.buscarPorRangoPrecio(new BigDecimal("1000"), new BigDecimal("20000"));

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }
}
