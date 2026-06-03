package mx.com.adrian.demo.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO que encapsula los datos de salida de un producto.
 * Se usa en las respuestas de la API para evitar exponer la entidad directamente.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoResponseDTO {

    @Schema(description = "Identificador único del producto", example = "1")
    private Long id;

    @Schema(description = "Nombre del producto", example = "Laptop Gamer")
    private String nombre;

    @Schema(description = "Descripción detallada", example = "Laptop con RTX 4060 y 32GB RAM")
    private String descripcion;

    @Schema(description = "Precio unitario", example = "15000.00")
    private BigDecimal precio;

    @Schema(description = "Cantidad en inventario", example = "25")
    private Integer cantidadStock;

    @Schema(description = "Categoría del producto", example = "Electrónica")
    private String categoria;

    @Schema(description = "Fecha de creación del registro", example = "2026-06-03T10:30:00")
    private LocalDateTime fechaCreacion;

    @Schema(description = "Indica si el producto está activo", example = "true")
    private Boolean activo;
}
