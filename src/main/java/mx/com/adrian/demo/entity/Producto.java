package mx.com.adrian.demo.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad que representa un producto en el catálogo de e-commerce.
 * Mapea la tabla {@code productos} y contiene la información
 * básica del producto: nombre, descripción, precio, stock, categoría y estado.
 */
@Entity
@Table(name = "productos")
@Data
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único del producto", example = "1")
    private Long id;

    @Schema(description = "Nombre del producto", example = "Laptop Gamer")
    @Column(nullable = false, length = 100)
    private String nombre;

    @Schema(description = "Descripción detallada del producto", example = "Laptop con RTX 4060")
    @Column(length = 500)
    private String descripcion;

    @Schema(description = "Precio unitario del producto", example = "15000.00")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @Schema(description = "Cantidad disponible en inventario", example = "25")
    @Column(name = "cantidad_stock", nullable = false)
    private Integer cantidadStock;

    @Schema(description = "Categoría a la que pertenece el producto", example = "Electrónica")
    @Column(nullable = false, length = 50)
    private String categoria;

    @Schema(description = "Fecha y hora de creación del registro", example = "2026-06-03T10:30:00")
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Schema(description = "Indica si el producto está activo (true) o eliminado lógicamente (false)", example = "true")
    @Column(nullable = false)
    private Boolean activo = true;

    /**
     * Asigna valores por defecto antes de persistir la entidad por primera vez.
     * Establece la fecha de creación y activa el producto si no se definió explícitamente.
     */
    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
        if (this.activo == null) {
            this.activo = true;
        }
    }
}
