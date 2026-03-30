package tn.esprit._4se2.pi.dto;

import jakarta.validation.constraints.*;
public class RestaurantDTOs {

    public class RestaurantRequestDTO {

        @NotBlank(message = "Le nom du restaurant est requis")
        @Size(min = 2, max = 100, message = "Le nom doit contenir entre 2 et 100 caractères")
        private String name;

        private String type;

        @NotBlank(message = "L'emplacement est requis")
        private String location;

        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
    }

    public class RestaurantDTO {
        private Long id;
        private String name;
        private String type;
        private String location;

        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
    }
}
