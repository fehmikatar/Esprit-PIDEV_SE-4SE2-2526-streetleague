package tn.esprit._4se2.pi.dto;

import jakarta.validation.constraints.*;
public class HotelDTOs {

    public class HotelRequestDTO {

        @NotBlank(message = "Le nom de l'hôtel est requis")
        @Size(min = 2, max = 100, message = "Le nom doit contenir entre 2 et 100 caractères")
        private String name;

        @NotBlank(message = "L'emplacement est requis")
        private String location;

        @Positive(message = "Le prix doit être positif")
        private Double price;

        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }

        public Double getPrice() { return price; }
        public void setPrice(Double price) { this.price = price; }
    }

    public class HotelDTO {
        private Long id;
        private String name;
        private String location;
        private Double price;

        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }

        public Double getPrice() { return price; }
        public void setPrice(Double price) { this.price = price; }
    }
}
