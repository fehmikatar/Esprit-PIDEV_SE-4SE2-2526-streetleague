package tn.esprit._4se2.pi.Swagger;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.OpenAPI;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "⚽ StreetLeague API – Mahdi Naifer",
                version = "1.0.0",
                description = """
                        <div style='background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); padding: 20px; border-radius: 10px; color: white; margin-bottom: 20px;'>
                            <h2 style='margin: 0;'>🏆 Gestion des Compétitions et Matchs</h2>
                            <p style='margin: 10px 0 0 0; opacity: 0.9;'>Bienvenue sur l'API de gestion des compétitions, matchs et événements.</p>
                        </div>
                        
                        ### 📋 Modules Disponibles
                        | Module | Description |
                        |--------|-------------|
                        | **🏆 Compétitions** | Gestion des compétitions sportives |
                        | **⚽ Matchs** | Gestion des matchs de compétition |
                        | **📋 Événements de Match** | Gestion des événements durant les matchs |
                        | **📝 Inscriptions** | Gestion des inscriptions des équipes |
                        | **👥 Equipes** | Gestion des informations des équipes |
                        
                        ---
                        *Développé avec Spring Boot & SpringDoc OpenAPI 3.0*
                        """,
                contact = @Contact(
                        name = "👨‍💻 Mahdi Naifer - Software Engineering Student",
                        email = "mahdi.naifer@esprit.tn",
                        url = "https://www.linkedin.com/in/mahdi-naifer"
                ),
                license = @License(
                        name = "📜 Apache License 2.0",
                        url = "https://www.apache.org/licenses/LICENSE-2.0"
                )
        ),
        servers = {
                @Server(url = "http://localhost:8080", description = "💻 Serveur Local (Développement)"),
                @Server(url = "https://api.streetleague.com", description = "🌍 Serveur de Production")
        }
)
public class Swagger {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new io.swagger.v3.oas.models.info.Info()
                        .title("⚽ StreetLeague API")
                        .version("1.0.0")
                        .description("API pour la gestion des compétitions, matchs et événements dans StreetLeague"));
    }

    // Groupes API (Modules)
    @Bean public GroupedOpenApi competitionsApi() { return GroupedOpenApi.builder().group("🏆 Compétitions").pathsToMatch("/api/competitions/**").build(); }
    @Bean public GroupedOpenApi matchesApi() { return GroupedOpenApi.builder().group("⚽ Matchs").pathsToMatch("/api/matches/**").build(); }
    @Bean public GroupedOpenApi matchEventsApi() { return GroupedOpenApi.builder().group("📋 Événements de Match").pathsToMatch("/api/match-events/**").build(); }
    @Bean public GroupedOpenApi registrationsApi() { return GroupedOpenApi.builder().group("📝 Inscriptions").pathsToMatch("/api/registrations/**").build(); }
    @Bean public GroupedOpenApi teamsApi() { return GroupedOpenApi.builder().group("👥 Equipes").pathsToMatch("/api/teams/**").build(); }
}
