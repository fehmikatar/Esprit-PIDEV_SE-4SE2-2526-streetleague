package tn.esprit.se2.laakommanel.pi.Swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("🏥 StreetLeague - API Healthcare")
                        .description("""
                                # API de Gestion Santé pour Athlètes Amateurs
                                
                                Cette API permet de gérer :
                                * **Profils santé** des athlètes (poids, taille, IMC, statut)
                                * **Dossiers médicaux** et historique des blessures
                                * **Rendez-vous** médicaux et suivis
                                * **Plans nutritionnels** personnalisés
                                
                                ## Fonctionnalités principales
                                * Création et gestion des profils santé
                                * Suivi des blessures et temps de récupération
                                * Prise de rendez-vous avec professionnels de santé
                                * Recommandations personnalisées basées sur l'IA
                                
                                ## Authentification
                                * Pour tester les endpoints protégés, utilisez le bouton **Authorize** en haut
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Équipe StreetLeague - Healthcare")
                                .email("healthcare@streetleague.com")
                                .url("https://streetleague.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://springdoc.org")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080/api/v1")
                                .description("Serveur de Développement"),
                        new Server()
                                .url("https://api.streetleague.com/api/v1")
                                .description("Serveur de Production")
                ))
                .tags(List.of(
                        new Tag().name("Health Profiles").description("📊 Gestion des profils santé des athlètes"),
                        new Tag().name("Medical Records").description("🏥 Gestion des dossiers médicaux et blessures"),
                        new Tag().name("Appointments").description("📅 Gestion des rendez-vous médicaux"),
                        new Tag().name("Diet Plans").description("🥗 Gestion des plans nutritionnels"),
                        new Tag().name("Health Dashboard").description("📈 Tableau de bord santé")
                ));
    }
}
