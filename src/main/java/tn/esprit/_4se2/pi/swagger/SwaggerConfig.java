package tn.esprit._4se2.pi.Swagger;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme; // Annotation pour configurer la sécurité JWT
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import io.swagger.v3.oas.models.ExternalDocumentation;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Configuration
@OpenAPIDefinition(info = @Info(title = "API du projet PI ESPRIT", version = "1.0"))
@SecurityScheme(  // Annotation pour définir un schéma de sécurité HTTP avec Bearer JWT
        name = "bearerAuth",  // Nom du schéma de sécurité
        type = SecuritySchemeType.HTTP,  // Type HTTP pour Bearer Token
        scheme = "bearer",  // Utilise le schéma "bearer"
        bearerFormat = "JWT"  // Format du token (JWT)
)
public class SwaggerConfig {

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
                                """)
                        .version("1.0.0")
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8085")
                                .description("Serveur local de développement")
                ))
                .tags(List.of(
                        new Tag().name("Users").description("Gestion des utilisateurs"),
                        new Tag().name("Authentication").description("Register & Login"),
                        new Tag().name("Field Owners").description("Gestion des propriétaires de terrains"),
                        new Tag().name("Players").description("Gestion des joueurs"),
                        new Tag().name("Sport Spaces").description("Gestion des terrains sportifs"),
                        new Tag().name("Bookings").description("Gestion des réservations"),
                        new Tag().name("Notifications").description("Gestion des notifications"),
                        new Tag().name("Teams").description("Gestion des équipes")
                ))
                .externalDocs(new ExternalDocumentation()
                        .description("Documentation complète du projet PI")
                        .url("https://github.com/esprit-4se2/pi"));
    }

    @Bean
    public GroupedOpenApi userApi() {
        return GroupedOpenApi.builder()
                .group("Users API")
                .pathsToMatch("/api/users/**")
                .build();
    }

    @Bean
    public GroupedOpenApi fieldOwnerApi() {
        return GroupedOpenApi.builder()
                .group("Field Owners API")
                .pathsToMatch("/api/field-owners/**")
                .build();
    }

    @Bean
    public GroupedOpenApi playerApi() {
        return GroupedOpenApi.builder()
                .group("Players API")
                .pathsToMatch("/api/players/**")
                .build();
    }

    @Bean
    public GroupedOpenApi sportSpaceApi() {
        return GroupedOpenApi.builder()
                .group("Sport Spaces API")
                .pathsToMatch("/api/sport-spaces/**")
                .build();
    }

    @Bean
    public GroupedOpenApi bookingApi() {
        return GroupedOpenApi.builder()
                .group("Bookings API")
                .pathsToMatch("/api/bookings/**")
                .build();
    }

    @Bean
    public GroupedOpenApi notificationApi() {
        return GroupedOpenApi.builder()
                .group("Notifications API")
                .pathsToMatch("/api/notifications/**")
                .build();
    }

    @Bean
    public GroupedOpenApi teamApi() {
        return GroupedOpenApi.builder()
                .group("Teams API")
                .pathsToMatch("/api/teams/**")
                .build();
    }
    @Bean
    public GroupedOpenApi authApi() {
        return GroupedOpenApi.builder()
                .group("Authentication API")
                .pathsToMatch("/api/auth/**")
                .build();
    }

    @Bean
    public GroupedOpenApi promotionApi() {
        return GroupedOpenApi.builder()
                .group("Promotions API")
                .pathsToMatch("/api/promotions/**")
                .build();
    }
    @Bean
    public GroupedOpenApi badgeApi() {
        return GroupedOpenApi.builder()
                .group("Badges API")
                .pathsToMatch("/api/badges/**")
                .build();
    }

    @Bean
    public GroupedOpenApi performanceApi() {
        return GroupedOpenApi.builder()
                .group("Performances API")
                .pathsToMatch("/api/performances/**")
                .build();
    }
    @Bean
    public GroupedOpenApi categoryApi() {
        return GroupedOpenApi.builder()
                .group("Categories API")
                .pathsToMatch("/api/categories/**")
                .build();
    }

    @Bean
    public GroupedOpenApi commentApi() {
        return GroupedOpenApi.builder()
                .group("Comments API")
                .pathsToMatch("/api/comments/**")
                .build();
    }

    @Bean
    public GroupedOpenApi likeApi() {
        return GroupedOpenApi.builder()
                .group("Likes API")
                .pathsToMatch("/api/likes/**")
                .build();
    }

    @Bean
    public GroupedOpenApi messageApi() {
        return GroupedOpenApi.builder()
                .group("Messages API")
                .pathsToMatch("/api/messages/**")
                .build();
    }

    @Bean
    public GroupedOpenApi postApi() {
        return GroupedOpenApi.builder()
                .group("Posts API")
                .pathsToMatch("/api/posts/**")
                .build();
    }

    @Bean
    public GroupedOpenApi teamMemberApi() {
        return GroupedOpenApi.builder()
                .group("Team Members API")
                .pathsToMatch("/api/team-members/**")
                .build();
    }
}