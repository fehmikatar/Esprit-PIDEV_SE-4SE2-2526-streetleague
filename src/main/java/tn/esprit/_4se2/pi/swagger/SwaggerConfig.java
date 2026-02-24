package tn.esprit._4se2.pi.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import io.swagger.v3.oas.models.ExternalDocumentation;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("🏟️ Sport Space API")
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
}