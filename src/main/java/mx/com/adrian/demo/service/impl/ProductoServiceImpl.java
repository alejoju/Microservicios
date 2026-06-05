package mx.com.adrian.demo.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.com.adrian.demo.entity.Producto;
import mx.com.adrian.demo.exception.ResourceNotFoundException;
import mx.com.adrian.demo.mapper.ProductoMapper;
import mx.com.adrian.demo.model.ProductoRequestDTO;
import mx.com.adrian.demo.model.ProductoResponseDTO;
import mx.com.adrian.demo.repository.ProductoRepository;
import mx.com.adrian.demo.service.ProductoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Implementación concreta del servicio de productos.
 * Orquesta las operaciones entre el repositorio JPA y el mapper MapStruct,
 * aplicando las reglas de negocio y registrando logs de cada operación.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository repository;
    private final ProductoMapper mapper;

    @Override
    @Transactional
    public ProductoResponseDTO crearProducto(ProductoRequestDTO request) {
        log.info("Creando producto: {}", request.getNombre());
        validarRequest(request);
        Producto producto = mapper.toEntity(request);
        producto = repository.save(producto);
        log.info("Producto creado con ID: {}", producto.getId());
        return mapper.toResponseDTO(producto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductoResponseDTO> listarProductos(Pageable pageable) {
        log.debug("Listando productos activos con paginación: {}", pageable);
        return repository.findByActivoTrue(pageable)
                .map(mapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductoResponseDTO obtenerProducto(Long id) {
        log.debug("Buscando producto por ID: {}", id);
        Producto producto = repository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));
        return mapper.toResponseDTO(producto);
    }

    @Override
    @Transactional
    public ProductoResponseDTO actualizarProducto(Long id, ProductoRequestDTO request) {
        log.info("Actualizando producto ID: {}", id);
        validarRequest(request);
        Producto producto = repository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));
        mapper.updateEntity(request, producto);
        producto = repository.save(producto);
        log.info("Producto actualizado ID: {}", id);
        return mapper.toResponseDTO(producto);
    }

    @Override
    @Transactional
    public void eliminarProducto(Long id) {
        log.info("Eliminando (lógico) producto ID: {}", id);
        Producto producto = repository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));
        producto.setActivo(false);
        repository.save(producto);
        log.info("Producto ID: {} marcado como inactivo", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> listarPorCategoria(String categoria) {
        log.debug("Listando productos por categoría: {}", categoria);
        return repository.findByCategoriaAndActivoTrue(categoria)
                .stream()
                .map(mapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> buscarPorRangoPrecio(BigDecimal min, BigDecimal max) {
        log.debug("Buscando productos por rango de precio: {} - {}", min, max);
        return repository.findByPrecioBetween(min, max)
                .stream()
                .map(mapper::toResponseDTO)
                .toList();
    }

    /**
     * Valida los datos de entrada del producto antes de procesarlo.
     *
     * @param request DTO con los datos a validar.
     * @throws IllegalArgumentException si algún campo no cumple las reglas de negocio.
     */
    private void validarRequest(ProductoRequestDTO request) {
        if (request.getNombre() == null || request.getNombre().trim().length() < 3) {
            throw new IllegalArgumentException("El nombre debe tener entre 3 y 100 caracteres");
        }
        if (request.getPrecio() == null || request.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El precio debe ser un valor positivo");
        }
        if (request.getCantidadStock() == null || request.getCantidadStock() < 0) {
            throw new IllegalArgumentException("La cantidad en stock no puede ser negativa");
        }
        if (request.getCategoria() == null || request.getCategoria().trim().isEmpty()) {
            throw new IllegalArgumentException("La categoría es obligatoria");
        }
    }

	@Override
	public List<ProductoResponseDTO> buscarNombre(String desc) {
		StringBuilder builder = new StringBuilder("%").append(desc).append("%");
		return repository.findByDescripcion(builder.toString().toUpperCase())
                .stream()
                .map(mapper::toResponseDTO)
                .toList();
	}
}
