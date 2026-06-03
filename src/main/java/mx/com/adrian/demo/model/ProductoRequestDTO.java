package mx.com.adrian.demo.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

/**
 * DTO que encapsula los datos de entrada para crear o actualizar un producto.
 * Incluye validaciones de negocio aplicadas mediante Bean Validation.
 */
@Data
public class ProductoRequestDTO {

    @Schema(description = "Nombre del producto", example = "Laptop Gamer", minLength = 3, maxLength = 100)
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    private String nombre;

    @Schema(description = "Descripción del producto", example = "Laptop con RTX 4060 y 32GB RAM", maxLength = 500)
    @Size(max = 500, message = "La descripción no debe exceder 500 caracteres")
    private String descripcion;

    @Schema(description = "Precio unitario del producto", example = "15000.00")
    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser positivo")
    @Digits(integer = 8, fraction = 2, message = "El precio debe tener hasta 2 decimales")
    private BigDecimal precio;

    @Schema(description = "Cantidad disponible en inventario", example = "25")
    @NotNull(message = "La cantidad en stock es obligatoria")
    @Min(value = 0, message = "La cantidad en stock no puede ser negativa")
    private Integer cantidadStock;

    @Schema(description = "Categoría del producto", example = "Electrónica")
    @NotBlank(message = "La categoría es obligatoria")
    private String categoria;
}
