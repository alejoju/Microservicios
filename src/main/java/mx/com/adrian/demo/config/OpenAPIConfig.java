package mx.com.adrian.demo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración personalizada de OpenAPI (Swagger).
 * Define el título, versión, descripción y licencia
 * que se muestran en la interfaz de Swagger UI.
 */
@Configuration
public class OpenAPIConfig {

    /**
     * Crea y configura el bean {@link OpenAPI} con la información
     * general de la API de productos.
     *
     * @return instancia de OpenAPI con la configuración definida.
     */
    @Bean
    public OpenAPI customOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Productos API")
                        .version("1.0.0")
                        .description("API para gestión de productos de e-commerce")
                        .license(new License()
                                .name("Venturessoft")
                                .url("https://www.venturessoft.com")));
    }
}
