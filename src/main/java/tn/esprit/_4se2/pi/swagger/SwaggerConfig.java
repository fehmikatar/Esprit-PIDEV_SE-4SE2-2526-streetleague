package tn.esprit._4se2.pi.Swagger;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme; // Annotation pour configurer la sécurité JWT
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "API du projet PI ESPRIT", version = "1.0"))
@SecurityScheme(  // Annotation pour définir un schéma de sécurité HTTP avec Bearer JWT
        name = "bearerAuth",  // Nom du schéma de sécurité
        type = SecuritySchemeType.HTTP,  // Type HTTP pour Bearer Token
        scheme = "bearer",  // Utilise le schéma "bearer"
        bearerFormat = "JWT"  // Format du token (JWT)
)
public class SwaggerConfig {

    // Définition de l'OpenAPI pour l'API
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new io.swagger.v3.oas.models.info.Info()
                        .title("🏟️ Sport Space API")  // Titre de l'API
                        .description("""
                                API du projet **PI ESPRIT**, permettant la gestion complète :
                                - des Utilisateurs
                                - des Terrains (Sport Spaces)
                                - des Réservations
                                - des Notifications
                                - des Équipes
                                """)  // Description de l'API
                        .version("1.0.0"));  // Version de l'API
    }
}