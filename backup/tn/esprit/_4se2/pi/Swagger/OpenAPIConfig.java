package tn.esprit._4se2.pi.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "🛍️ StreetLeague E-Commerce Platform",
                version = "1.0.0",
                description = """
                        <div style='background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); padding: 20px; border-radius: 10px; color: white; margin-bottom: 20px;'>
                            <h2 style='margin: 0;'>🏪 StreetLeague - E-Commerce API</h2>
                            <p style='margin: 10px 0 0 0; opacity: 0.9;'>Bienvenue sur l'interface de l'API StreetLeague.</p>
                        </div>
                        
                        ### 📋 Modules Disponibles
                        | Module | Description | Base URL |
                        |--------|-------------|----------|
                        | **📦 Produits** | Gestion complète du catalogue produits | `/api/products` |
                        | **📁 Catégories** | Organisation des produits par catégories | `/api/categories` |
                        | **❤️ Favoris** | Gestion des listes de favoris utilisateurs | `/api/favorites` |
                        | **🛒 Panier** | Gestion du panier d'achat | `/api/cart` |
                        | **🏷️ Codes Promo** | Gestion des promotions et codes promo | `/api/promocodes` |
                        
                        ---
                        *Développé avec Spring Boot & SpringDoc OpenAPI 3.0*
                        """,
                contact = @Contact(
                        name = "👩‍💻 Ibtihel Baccari - Software Engineering Student",
                        email = "ibtihel.baccari@esprit.tn",
                        url = "https://www.linkedin.com/in/ibtihel-baccari"
                ),
                license = @License(
                        name = "📝 MIT License",
                        url = "https://opensource.org/licenses/MIT"
                )
        ),
        servers = {
                @Server(
                        url = "http://localhost:8085",
                        description = "🖥️ Serveur de Développement Local"
                )
        }
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "Authentification JWT - Entrez votre token: Bearer <token>"
)
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new io.swagger.v3.oas.models.info.Info()
                        .title("🛍️ StreetLeague E-Commerce API")
                        .description("API structurée par modules - Sélectionnez un module dans la liste déroulante")
                        .version("1.0.0"));
    }

    @Bean
    public GroupedOpenApi productGroup() {
        return GroupedOpenApi.builder()
                .group("📦 Produits")
                .pathsToMatch("/api/products/**")
                .build();
    }

    @Bean
    public GroupedOpenApi categoryGroup() {
        return GroupedOpenApi.builder()
                .group("📁 Catégories")
                .pathsToMatch("/api/categories/**")
                .build();
    }

    @Bean
    public GroupedOpenApi favoriteGroup() {
        return GroupedOpenApi.builder()
                .group("❤️ Favoris")
                .pathsToMatch("/api/favorites/**")
                .build();
    }

    @Bean
    public GroupedOpenApi cartGroup() {
        return GroupedOpenApi.builder()
                .group("🛒 Panier")
                .pathsToMatch("/api/cart/**")
                .build();
    }

    @Bean
    public GroupedOpenApi promoCodeGroup() {
        return GroupedOpenApi.builder()
                .group("🏷️ Codes Promo")
                .pathsToMatch("/api/promocodes/**")
                .build();
    }

    @Bean
    public GroupedOpenApi allEndpointsGroup() {
        return GroupedOpenApi.builder()
                .group("📋 Tous les endpoints")
                .pathsToMatch("/api/**")
                .build();
    }
}