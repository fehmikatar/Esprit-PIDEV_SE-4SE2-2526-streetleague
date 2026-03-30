package tn.esprit._4se2.pi.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "collaborations")
public class Collaboration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "fournisseur_eau_id", nullable = false)
    private FournisseurEau fournisseurEau;

    @ManyToOne
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @ManyToOne
    @JoinColumn(name = "competition_id", nullable = false)
    private Competition competition;

    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = true)
    private Restaurant restaurant;

    @Column(nullable = false)
    private Double prixGros;

    @Column(nullable = false)
    private String publiciteObjet;

    @Column(nullable = false)
    private String description;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public FournisseurEau getFournisseurEau() {
        return fournisseurEau;
    }

    public void setFournisseurEau(FournisseurEau fournisseurEau) {
        this.fournisseurEau = fournisseurEau;
    }

    public Hotel getHotel() {
        return hotel;
    }

    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
    }

    public Competition getCompetition() {
        return competition;
    }

    public void setCompetition(Competition competition) {
        this.competition = competition;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public Double getPrixGros() {
        return prixGros;
    }

    public void setPrixGros(Double prixGros) {
        this.prixGros = prixGros;
    }

    public String getPubliciteObjet() {
        return publiciteObjet;
    }

    public void setPubliciteObjet(String publiciteObjet) {
        this.publiciteObjet = publiciteObjet;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
