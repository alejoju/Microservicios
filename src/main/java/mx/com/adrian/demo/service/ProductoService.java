package mx.com.adrian.demo.service;

import mx.com.adrian.demo.model.ProductoRequestDTO;
import mx.com.adrian.demo.model.ProductoResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

/**
 * Servicio que define las operaciones de negocio para la gestión de productos.
 * Incluye creación, consulta, actualización y eliminación lógica de productos,
 * así como filtros por categoría y rango de precio.
 */
public interface ProductoService {

    /**
     * Crea un nuevo producto a partir de los datos proporcionados.
     *
     * @param request DTO con los datos del producto a crear.
     * @return DTO con los datos del producto creado, incluyendo su ID generado.
     */
    ProductoResponseDTO crearProducto(ProductoRequestDTO request);

    /**
     * Retorna una página de productos activos, ordenados y paginados.
     *
     * @param pageable configuración de paginación y ordenamiento.
     * @return página con los productos activos encontrados.
     */
    Page<ProductoResponseDTO> listarProductos(Pageable pageable);

    /**
     * Busca un producto activo por su identificador único.
     *
     * @param id identificador del producto.
     * @return DTO con los datos del producto.
     * @throws mx.com.adrian.demo.exception.ResourceNotFoundException si el producto no existe o está inactivo.
     */
    ProductoResponseDTO obtenerProducto(Long id);

    /**
     * Actualiza los datos de un producto existente.
     *
     * @param id      identificador del producto a actualizar.
     * @param request DTO con los nuevos datos.
     * @return DTO con los datos actualizados del producto.
     * @throws mx.com.adrian.demo.exception.ResourceNotFoundException si el producto no existe o está inactivo.
     */
    ProductoResponseDTO actualizarProducto(Long id, ProductoRequestDTO request);

    /**
     * Realiza la eliminación lógica de un producto marcándolo como inactivo.
     *
     * @param id identificador del producto a eliminar.
     * @throws mx.com.adrian.demo.exception.ResourceNotFoundException si el producto no existe o está inactivo.
     */
    void eliminarProducto(Long id);

    /**
     * Retorna todos los productos activos que pertenecen a una categoría.
     *
     * @param categoria nombre de la categoría a filtrar.
     * @return lista de productos activos de esa categoría.
     */
    List<ProductoResponseDTO> listarPorCategoria(String categoria);

    /**
     * Retorna los productos activos cuyo precio está dentro del rango especificado.
     *
     * @param min límite inferior del rango de precio.
     * @param max límite superior del rango de precio.
     * @return lista de productos activos dentro del rango.
     */
    List<ProductoResponseDTO> buscarPorRangoPrecio(BigDecimal min, BigDecimal max);
}
