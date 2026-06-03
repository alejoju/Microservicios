package mx.com.adrian.demo.exception;

/**
 * Excepción lanzada cuando no se encuentra un recurso solicitado en la base de datos.
 * Se utiliza en los servicios para indicar que una entidad no existe o no está activa,
 * y es capturada por {@link GlobalExceptionHandler} para retornar HTTP 404.
 */
public class ResourceNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

	/**
     * Crea una excepción con un mensaje descriptivo del error.
     *
     * @param message descripción del recurso no encontrado.
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * Crea una excepción con mensaje y causa raíz.
     *
     * @param message descripción del recurso no encontrado.
     * @param cause   causa original de la excepción.
     */
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
