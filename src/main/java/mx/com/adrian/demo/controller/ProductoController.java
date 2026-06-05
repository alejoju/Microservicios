package mx.com.adrian.demo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mx.com.adrian.demo.model.ProductoRequestDTO;
import mx.com.adrian.demo.model.ProductoResponseDTO;
import mx.com.adrian.demo.service.ProductoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Controlador REST que expone los endpoints para la gestión de productos.
 * Todas las operaciones están documentadas con OpenAPI y los métodos
 * delegan la lógica de negocio en {@link ProductoService}.
 */
@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
@Tag(name = "Productos", description = "API para gestión de productos de e-commerce")
public class ProductoController {

    private final ProductoService productoService;

    /**
     * Crea un nuevo producto en el catálogo.
     *
     * @param request DTO con los datos del producto a crear.
     * @return el producto creado con código HTTP 201 (Created).
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Crear un nuevo producto", description = "Registra un producto en el catálogo y retorna sus datos con el ID asignado.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Producto creado exitosamente",
                content = @Content(schema = @Schema(implementation = ProductoResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    public ResponseEntity<ProductoResponseDTO> crearProducto(
            @Valid @RequestBody
            @Parameter(description = "Datos del producto a crear", required = true)
            ProductoRequestDTO request) {
        ProductoResponseDTO response = productoService.crearProducto(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Retorna una lista paginada de los productos activos.
     *
     * @param pageable parámetros de paginación y ordenamiento (por defecto tamaño 10, ordenado por nombre).
     * @return página con los productos activos.
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Listar productos activos", description = "Retorna los productos activos con paginación y ordenamiento.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente",
                content = @Content(schema = @Schema(implementation = Page.class)))
    })
    public ResponseEntity<Page<ProductoResponseDTO>> listarProductos(
            @PageableDefault(size = 10, sort = "nombre")
            @Parameter(description = "Parámetros de paginación")
            Pageable pageable) {
        return ResponseEntity.ok(productoService.listarProductos(pageable));
    }

    /**
     * Obtiene un producto activo por su identificador único.
     *
     * @param id identificador del producto.
     * @return el producto encontrado.
     */
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Obtener producto por ID", description = "Busca un producto activo por su identificador único.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Producto encontrado",
                content = @Content(schema = @Schema(implementation = ProductoResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    public ResponseEntity<ProductoResponseDTO> obtenerProducto(
            @PathVariable
            @Parameter(description = "Identificador del producto", example = "1", required = true)
            Long id) {
        return ResponseEntity.ok(productoService.obtenerProducto(id));
    }

    /**
     * Actualiza los datos de un producto existente.
     *
     * @param id      identificador del producto a actualizar.
     * @param request DTO con los nuevos datos.
     * @return el producto actualizado.
     */
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Actualizar producto", description = "Actualiza los datos de un producto existente.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Producto actualizado correctamente",
                content = @Content(schema = @Schema(implementation = ProductoResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    public ResponseEntity<ProductoResponseDTO> actualizarProducto(
            @PathVariable
            @Parameter(description = "Identificador del producto", example = "1", required = true)
            Long id,
            @Valid @RequestBody
            @Parameter(description = "Datos actualizados del producto", required = true)
            ProductoRequestDTO request) {
        return ResponseEntity.ok(productoService.actualizarProducto(id, request));
    }

    /**
     * Realiza la eliminación lógica de un producto (lo marca como inactivo).
     *
     * @param id identificador del producto a eliminar.
     * @return respuesta vacía con código HTTP 204 (No Content).
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar producto", description = "Marca un producto como inactivo (borrado lógico).")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Producto eliminado lógicamente"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    public ResponseEntity<Void> eliminarProducto(
            @PathVariable
            @Parameter(description = "Identificador del producto", example = "1", required = true)
            Long id) {
        productoService.eliminarProducto(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Retorna todos los productos activos de una categoría específica.
     *
     * @param categoria nombre de la categoría a filtrar.
     * @return lista de productos activos de esa categoría.
     */
    @GetMapping(value = "/categoria/{categoria}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Filtrar por categoría", description = "Obtiene todos los productos activos de una categoría.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de productos por categoría",
                content = @Content(schema = @Schema(implementation = List.class)))
    })
    public ResponseEntity<List<ProductoResponseDTO>> listarPorCategoria(
            @PathVariable
            @Parameter(description = "Nombre de la categoría", example = "Electrónica", required = true)
            String categoria) {
        return ResponseEntity.ok(productoService.listarPorCategoria(categoria));
    }

    /**
     * Busca productos activos cuyo precio esté dentro del rango especificado.
     *
     * @param precioMin límite inferior del rango.
     * @param precioMax límite superior del rango.
     * @return lista de productos activos dentro del rango de precio.
     */
    @GetMapping(value = "/buscar", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Buscar por rango de precio", description = "Filtra productos activos cuyo precio esté entre precioMin y precioMax.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de productos en el rango de precio",
                content = @Content(schema = @Schema(implementation = List.class)))
    })
    public ResponseEntity<List<ProductoResponseDTO>> buscarPorRangoPrecio(
            @RequestParam
            @Parameter(description = "Precio mínimo", example = "1000", required = true)
            BigDecimal precioMin,
            @RequestParam
            @Parameter(description = "Precio máximo", example = "50000", required = true)
            BigDecimal precioMax) {
        return ResponseEntity.ok(productoService.buscarPorRangoPrecio(precioMin, precioMax));
    }
    
    @GetMapping(value = "/buscar-descripcion", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Buscar por rango de precio", description = "Filtra productos activos cuyo precio esté entre precioMin y precioMax.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de productos en el rango de precio",
                content = @Content(schema = @Schema(implementation = List.class)))
    })
    public ResponseEntity<List<ProductoResponseDTO>> buscarnombre(
            @RequestParam
            @Parameter(description = "Descripción", example = "Lap", required = true)
            String desc) {
        return ResponseEntity.ok(productoService.buscarNombre(desc));
    }
}
