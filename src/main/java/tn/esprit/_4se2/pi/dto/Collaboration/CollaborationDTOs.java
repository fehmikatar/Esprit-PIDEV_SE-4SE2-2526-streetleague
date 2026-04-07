package tn.esprit._4se2.pi.dto.Collaboration;

import jakarta.validation.constraints.*;
import tn.esprit._4se2.pi.dto.Competition.CompetitionDTOs;
import tn.esprit._4se2.pi.dto.FournisseurEau.FournisseurEauDTOs;
import tn.esprit._4se2.pi.dto.Hotel.HotelDTOs;
import tn.esprit._4se2.pi.dto.Restaurant.RestaurantDTOs;

public class CollaborationDTOs {


    // Request DTO
    public class CollaborationRequestDTO {

        @NotNull(message = "L'ID du fournisseur d'eau est requis")
        private Long fournisseurEauId;

        @NotNull(message = "L'ID de l'hôtel est requis")
        private Long hotelId;

        @NotNull(message = "L'ID de la compétition est requis")
        private Long competitionId;

        private Long restaurantId;

        @NotNull(message = "Le prix de gros est requis")
        @Positive(message = "Le prix de gros doit être positif")
        private Double prixGros;

        @NotBlank(message = "L'objet publicitaire est requis")
        @Size(max = 255, message = "L'objet publicitaire ne peut pas dépasser 255 caractères")
        private String publiciteObjet;

        @NotBlank(message = "La description est requise")
        @Size(max = 1000, message = "La description ne peut pas dépasser 1000 caractères")
        private String description;

        // Getters and Setters
        public Long getFournisseurEauId() { return fournisseurEauId; }
        public void setFournisseurEauId(Long fournisseurEauId) { this.fournisseurEauId = fournisseurEauId; }

        public Long getHotelId() { return hotelId; }
        public void setHotelId(Long hotelId) { this.hotelId = hotelId; }

        public Long getCompetitionId() { return competitionId; }
        public void setCompetitionId(Long competitionId) { this.competitionId = competitionId; }

        public Long getRestaurantId() { return restaurantId; }
        public void setRestaurantId(Long restaurantId) { this.restaurantId = restaurantId; }

        public Double getPrixGros() { return prixGros; }
        public void setPrixGros(Double prixGros) { this.prixGros = prixGros; }

        public String getPubliciteObjet() { return publiciteObjet; }
        public void setPubliciteObjet(String publiciteObjet) { this.publiciteObjet = publiciteObjet; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }

    // Response DTO
    public class CollaborationResponseDTO {
        private Long id;
        private FournisseurEauDTOs.FournisseurEauDTO fournisseurEau;
        private HotelDTOs.HotelDTO hotel;
        private CompetitionDTOs.CompetitionResponseDTO competition;
        private RestaurantDTOs.RestaurantDTO restaurant;
        private Double prixGros;
        private String publiciteObjet;
        private String description;

        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public FournisseurEauDTOs.FournisseurEauDTO getFournisseurEau() { return fournisseurEau; }
        public void setFournisseurEau(FournisseurEauDTOs.FournisseurEauDTO fournisseurEau) { this.fournisseurEau = fournisseurEau; }

        public HotelDTOs.HotelDTO getHotel() { return hotel; }
        public void setHotel(HotelDTOs.HotelDTO hotel) { this.hotel = hotel; }

        public CompetitionDTOs.CompetitionResponseDTO getCompetition() { return competition; }
        public void setCompetition(CompetitionDTOs.CompetitionResponseDTO competition) { this.competition = competition; }

        public RestaurantDTOs.RestaurantDTO getRestaurant() { return restaurant; }
        public void setRestaurant(RestaurantDTOs.RestaurantDTO restaurant) { this.restaurant = restaurant; }

        public Double getPrixGros() { return prixGros; }
        public void setPrixGros(Double prixGros) { this.prixGros = prixGros; }

        public String getPubliciteObjet() { return publiciteObjet; }
        public void setPubliciteObjet(String publiciteObjet) { this.publiciteObjet = publiciteObjet; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
}
