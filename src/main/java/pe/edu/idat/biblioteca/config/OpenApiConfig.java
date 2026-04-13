package pe.edu.idat.biblioteca.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de Swagger / OpenAPI 3.0 para documentación de la API.
 * Incluye esquema de seguridad Bearer JWT para probar endpoints protegidos.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "Bearer JWT";

        return new OpenAPI()
                // Información de la API
                .info(new Info()
                        .title("📚 API de Biblioteca Universitaria")
                        .version("1.0.0")
                        .description("""
                                API RESTful para la gestión integral de bibliotecas universitarias.
                                
                                **Funcionalidades:**
                                - 📖 Gestión de catálogo de libros (CRUD)
                                - 👤 Registro y gestión de usuarios
                                - 📋 Préstamos y devoluciones
                                - 🔐 Autenticación JWT con roles (ADMIN, USUARIO)
                                
                                **Credenciales de prueba:**
                                - Admin: admin@biblioteca.edu.pe / Admin123!
                                """)
                        .contact(new Contact()
                                .name("Equipo de Desarrollo IDAT")
                                .email("contacto@idat.edu.pe"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))

                // Esquema de seguridad Bearer JWT
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Ingrese el token JWT obtenido del endpoint /api/auth/login")));
    }
}
