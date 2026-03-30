package tn.esprit._4se2.pi.dto.FournisseurEau;

import jakarta.validation.constraints.*;
public class FournisseurEauDTOs {

    public class FournisseurEauRequestDTO {

        @NotBlank(message = "Le nom du fournisseur est requis")
        @Size(min = 2, max = 100, message = "Le nom doit contenir entre 2 et 100 caractères")
        private String name;

        @NotBlank(message = "L'adresse est requise")
        private String address;

        @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Le numéro de contact doit être valide")
        private String contact;

        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }

        public String getContact() { return contact; }
        public void setContact(String contact) { this.contact = contact; }
    }

    public class FournisseurEauDTO {
        private Long id;
        private String name;
        private String address;
        private String contact;

        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }

        public String getContact() { return contact; }
        public void setContact(String contact) { this.contact = contact; }
    }
}
