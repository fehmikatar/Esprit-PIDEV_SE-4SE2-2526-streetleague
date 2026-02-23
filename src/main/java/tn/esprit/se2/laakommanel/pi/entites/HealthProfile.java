package tn.esprit.se2.laakommanel.pi.entites;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "health_profiles")
public class HealthProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    private Double weight;
    private Double height;
    private Integer age;
    private String sportPosition;

    @Enumerated(EnumType.STRING)
    private FitnessStatus fitnessStatus;

    private LocalDate lastUpdated;
    private String emergencyContact;
    private String emergencyPhone;
    private String bloodType;
    private String allergies;
    private String medicalConditions;

    @OneToMany(mappedBy = "healthProfile", cascade = CascadeType.ALL)
    private List<MedicalRecord> medicalRecords;

    @OneToMany(mappedBy = "healthProfile", cascade = CascadeType.ALL)
    private List<HealthMetrics> healthMetrics;

    @OneToMany(mappedBy = "healthProfile", cascade = CascadeType.ALL)
    private List<DietPlan> dietPlans;

    // Calcul automatique du BMI
    @Transient
    public Double getBmi() {
        if (height != null && weight != null && height > 0) {
            return Math.round((weight / ((height/100) * (height/100))) * 100.0) / 100.0;
        }
        return null;
    }

    @Transient
    public String getBmiCategory() {
        Double bmi = getBmi();
        if (bmi == null) return "Non disponible";
        if (bmi < 18.5) return "Insuffisance pondérale";
        if (bmi < 25) return "Poids normal";
        if (bmi < 30) return "Surpoids";
        return "Obésité";
    }

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        lastUpdated = LocalDate.now();
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }

    public Double getHeight() { return height; }
    public void setHeight(Double height) { this.height = height; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public String getSportPosition() { return sportPosition; }
    public void setSportPosition(String sportPosition) { this.sportPosition = sportPosition; }

    public FitnessStatus getFitnessStatus() { return fitnessStatus; }
    public void setFitnessStatus(FitnessStatus fitnessStatus) { this.fitnessStatus = fitnessStatus; }

    public LocalDate getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDate lastUpdated) { this.lastUpdated = lastUpdated; }

    public String getEmergencyContact() { return emergencyContact; }
    public void setEmergencyContact(String emergencyContact) { this.emergencyContact = emergencyContact; }

    public String getEmergencyPhone() { return emergencyPhone; }
    public void setEmergencyPhone(String emergencyPhone) { this.emergencyPhone = emergencyPhone; }

    public String getBloodType() { return bloodType; }
    public void setBloodType(String bloodType) { this.bloodType = bloodType; }

    public String getAllergies() { return allergies; }
    public void setAllergies(String allergies) { this.allergies = allergies; }

    public String getMedicalConditions() { return medicalConditions; }
    public void setMedicalConditions(String medicalConditions) { this.medicalConditions = medicalConditions; }

    public List<MedicalRecord> getMedicalRecords() { return medicalRecords; }
    public void setMedicalRecords(List<MedicalRecord> medicalRecords) { this.medicalRecords = medicalRecords; }

    public List<HealthMetrics> getHealthMetrics() { return healthMetrics; }
    public void setHealthMetrics(List<HealthMetrics> healthMetrics) { this.healthMetrics = healthMetrics; }

    public List<DietPlan> getDietPlans() { return dietPlans; }
    public void setDietPlans(List<DietPlan> dietPlans) { this.dietPlans = dietPlans; }
}