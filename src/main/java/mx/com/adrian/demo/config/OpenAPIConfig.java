package mx.com.adrian.demo.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;

/**
 * Configuraci�n de OpenAPI.
 */
@Configuration
public class OpenAPIConfig {

    /**
     * Nombre de etiqueta para la autorizaci�n por JWT.
     */
    private static final String AUTH_KEY = "Authorization Bearer JWT";

    /**
     * Titulo/Nombre de la aplicaci�n.
     */
    @Value("${application.title: Titulo del microservicio}")
    private String title;

    /**
     * Descripci�n de la aplicaci�n.
     */
    @Value("${application.description: Descripci�n del microservicio}")
    private String description;

    /**
     * Versi�n de la aplicaci�n.
     */
    @Value("${application.version:1.0.0}")
    private String version;

    /**
     * Bean de {@link OpenAPI}.
     *
     * @return Bean de {@link OpenAPI}
     */
    @Bean
    public OpenAPI customOpenApi() {
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList(AUTH_KEY))
                .components(getComponents())
                .info(getInfo());
    }

    /**
     * Obtiene los componentes.
     *
     * @return los componentes
     */
    private static Components getComponents() {
        return new Components()
                .addSecuritySchemes(AUTH_KEY,
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT"));
    }

    /**
     * Obtiene la informaci�n.
     *
     * @return la informaci�n
     */
    private Info getInfo() {
        return new Info()
                .title(title)
                .version(version)
                .description(description)
                .license(getLicense());
    }

    /**
     * Obtiene la licencia.
     *
     * @return la licencia
     */
    private static License getLicense() {
        return new License()
                .name("Shabadabada.").url("https://www.alejoju.com.mx");
    }
    
    /**
     * Customiza el api.
     *  
     * @return configuracion
     */
//    @Bean
//	public OpenApiCustomizer openApiCustomizer() {
//		return openApi -> openApi.getServers().forEach(server -> {
//			if (server.getUrl().startsWith("http://")) {
//				server.setUrl(server.getUrl().replace("http://", "https://"));
//			}
//		});
//	}
        
    /**
     * Bean para personalizar las operaciones y ocultar par�metros inyectados por Spring.
     *
     * @return Un {@link OperationCustomizer} que oculta par�metros espec�ficos de la documentaci�n de Swagger.
     */
    @Bean
    public OperationCustomizer customizarOperacion() {
        return (Operation operation, HandlerMethod handlerMethod) -> {
            
            // Si la operaci�n no tiene par�metros, no hacemos nada.
            if (operation.getParameters() == null) {
                return operation;
            }

            // Elimina cualquier par�metro cuyo nombre sea "usuarioSesion".
            // Esto oculta el @CurrentUser final Usuario usuarioSesion de la documentaci�n.
            operation.getParameters().removeIf(parameter -> 
                "usuarioSesion".equals(parameter.getName())
            );

            return operation;
        };
    }           

}