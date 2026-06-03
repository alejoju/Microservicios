package mx.com.adrian.demo.repository;

import mx.com.adrian.demo.entity.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para la entidad {@link Producto}.
 * Proporciona operaciones CRUD y métodos de consulta adicionales
 * para filtrar productos activos por diferentes criterios.
 */
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    /**
     * Retorna una página de productos activos ordenados según el parámetro {@code pageable}.
     *
     * @param pageable objeto con la configuración de paginación y ordenamiento.
     * @return página con los productos activos encontrados.
     */
    Page<Producto> findByActivoTrue(Pageable pageable);

    /**
     * Busca un producto activo por su identificador único.
     *
     * @param id identificador del producto.
     * @return un {@code Optional} con el producto si existe y está activo, vacío en caso contrario.
     */
    Optional<Producto> findByIdAndActivoTrue(Long id);

    /**
     * Retorna todos los productos activos que pertenecen a una categoría específica.
     *
     * @param categoria nombre de la categoría a filtrar.
     * @return lista de productos activos de la categoría indicada.
     */
    List<Producto> findByCategoriaAndActivoTrue(String categoria);

    /**
     * Retorna los productos activos cuyo precio está dentro del rango especificado.
     *
     * @param min precio mínimo del rango (inclusive).
     * @param max precio máximo del rango (inclusive).
     * @return lista de productos activos dentro del rango de precio.
     */
    @Query("SELECT p FROM Producto p WHERE p.activo = true AND p.precio BETWEEN :min AND :max")
    List<Producto> findByPrecioBetween(@Param("min") BigDecimal min, @Param("max") BigDecimal max);
}
