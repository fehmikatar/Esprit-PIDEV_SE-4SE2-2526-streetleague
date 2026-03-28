package tn.esprit._4se2.pi.Swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springdoc.core.models.GroupedOpenApi;

import java.util.List;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("🏥 StreetLeague - Healthcare Module API")
                        .description("""
                            **Interactive documentation for the Healthcare Module** 🏥
                            
                            ---
                            ### Available Features :
                            * 📅 **Appointment Management** - Schedule and manage medical appointments
           
                            * 👤 **User Management** - Handle patients and system users
                            * 📊 **Health Profiles** - Track patient health information
                            * 📈 **Health Metrics** - Monitor vital signs and fitness data
                            * 📋 **Medical Records** - Store medical history and diagnoses
                            * 🥗 **Diet Plans** - Create and manage nutrition plans
                            
                            ---
                            ### Base URL: `http://localhost:8085/api`
                            
                            All endpoints require authentication except Swagger UI.
                            Use the **Authorize** button (🔓) to login.
                            """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Laakom Manel")
                                .email("manel.laakom@esprit.tn")
                                .url("https://www.esprit.tn"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://springdoc.org"))
                        .termsOfService("http://swagger.io/terms/"))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8085")
                                .description("Local Development Server"),
                        new Server()
                                .url("https://api.streetleague.com")
                                .description("Production Server (Coming Soon)")
                ))
                .tags(List.of(
                        new Tag().name("Users").description("👤 User management operations"),
                        new Tag().name("Appointments").description("📅 Appointment scheduling and management"),
                        new Tag().name("Health Profiles").description("📊 Patient health profiles"),
                        new Tag().name("Health Metrics").description("📈 Vital signs and fitness metrics"),
                        new Tag().name("Medical Records").description("📋 Medical history and diagnoses"),
                        new Tag().name("Diet Plans").description("🥗 Nutrition and diet plans")
                ));
    }

    // ✅ Group for Users
    @Bean
    public GroupedOpenApi userGroup() {
        return GroupedOpenApi.builder()
                .group("👤 Users")
                .pathsToMatch("/api/users/**")
                .displayName("User Management API")
                .build();
    }


    // ✅ Group for Appointments
    @Bean
    public GroupedOpenApi appointmentGroup() {
        return GroupedOpenApi.builder()
                .group("📅 Appointments")
                .pathsToMatch("/api/appointments/**")
                .displayName("Appointment Management API")
                .build();
    }

    // ✅ Group for Health Profiles
    @Bean
    public GroupedOpenApi healthProfileGroup() {
        return GroupedOpenApi.builder()
                .group("📊 Health Profiles")
                .pathsToMatch("/api/health-profiles/**")
                .displayName("Health Profile API")
                .build();
    }

    // ✅ Group for Health Metrics
    @Bean
    public GroupedOpenApi healthMetricsGroup() {
        return GroupedOpenApi.builder()
                .group("📈 Health Metrics")
                .pathsToMatch("/api/health-metrics/**")
                .displayName("Health Metrics API")
                .build();
    }

    // ✅ Group for Medical Records
    @Bean
    public GroupedOpenApi medicalRecordGroup() {
        return GroupedOpenApi.builder()
                .group("📋 Medical Records")
                .pathsToMatch("/api/medical-records/**")
                .displayName("Medical Records API")
                .build();
    }

    // ✅ Group for Diet Plans
    @Bean
    public GroupedOpenApi dietPlanGroup() {
        return GroupedOpenApi.builder()
                .group("🥗 Diet Plans")
                .pathsToMatch("/api/diet-plans/**")
                .displayName("Diet Plans API")
                .build();
    }

    // ✅ Group for all APIs
    @Bean
    public GroupedOpenApi allApisGroup() {
        return GroupedOpenApi.builder()
                .group("🌍 All APIs")
                .pathsToMatch("/api/**")
                .displayName("Complete API Collection")
                .build();
    }
}