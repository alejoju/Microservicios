package mx.com.adrian.demo.mapper;

import mx.com.adrian.demo.entity.Producto;
import mx.com.adrian.demo.model.ProductoRequestDTO;
import mx.com.adrian.demo.model.ProductoResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * Mapper de MapStruct que convierte entre la entidad {@link Producto}
 * y los DTOs de entrada/salida ({@link ProductoRequestDTO}, {@link ProductoResponseDTO}).
 */
@Mapper(componentModel = "spring")
public interface ProductoMapper {

    /**
     * Convierte una entidad {@link Producto} en un DTO de respuesta.
     *
     * @param producto entidad a convertir.
     * @return DTO con los datos del producto.
     */
    ProductoResponseDTO toResponseDTO(Producto producto);

    /**
     * Convierte un DTO de petición en una entidad {@link Producto}.
     * Los campos {@code id}, {@code fechaCreacion} y {@code activo} se ignoran
     * porque los asigna la base de datos o el ciclo de vida de JPA.
     *
     * @param dto DTO con los datos de entrada.
     * @return nueva entidad Producto.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "activo", ignore = true)
    Producto toEntity(ProductoRequestDTO dto);

    /**
     * Actualiza los campos de una entidad existente {@link Producto}
     * a partir de un DTO de petición. Los campos {@code id},
     * {@code fechaCreacion} y {@code activo} no se modifican.
     *
     * @param dto      DTO con los nuevos valores.
     * @param producto entidad a actualizar.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "activo", ignore = true)
    void updateEntity(ProductoRequestDTO dto, @MappingTarget Producto producto);
}
