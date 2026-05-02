package tn.esprit._4se2.pi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class FileStorageConfig implements WebMvcConfigurer {

    public FileStorageConfig() {
        System.out.println("🔵 FileStorageConfig CONSTRUCTOR appelé !");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        System.out.println("🟢 addResourceHandlers() appelé !");
        
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
        
        System.out.println("✅ ResourceHandler configuré : /uploads/** -> file:uploads/");
    }
}