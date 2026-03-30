package tn.esprit._4se2.pi.entities;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "health_profiles")
public class HealthProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
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

    @OneToMany(mappedBy = "healthProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MedicalRecord> medicalRecords = new ArrayList<>();

    @OneToMany(mappedBy = "healthProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HealthMetrics> healthMetrics = new ArrayList<>();

    @OneToMany(mappedBy = "healthProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DietPlan> dietPlans = new ArrayList<>();

    @PrePersist
    @PreUpdate
    protected void validateUserRole() {
        if (user != null && !user.isPatient()) {
            throw new RuntimeException("Health profile can only be created for users with role PATIENT");
        }
        lastUpdated = LocalDate.now();
    }

    // Calcul du BMI
    @Transient
    public Double getBmi() {
        if (height != null && weight != null && height > 0) {
            return Math.round((weight / ((height/100) * (height/100))) * 100.0) / 100.0;
        }
        return null;
    }

    @Transient
    public String getBmiCategory() {  // ✅ Doit retourner String
        Double bmi = getBmi();
        if (bmi == null) return "Non disponible";
        if (bmi < 18.5) return "Insuffisance pondérale";
        if (bmi < 25) return "Poids normal";
        if (bmi < 30) return "Surpoids";
        return "Obésité";
    }

    // Méthodes helpers
    public void addMedicalRecord(MedicalRecord record) {
        medicalRecords.add(record);
        record.setHealthProfile(this);
    }

    public void removeMedicalRecord(MedicalRecord record) {
        medicalRecords.remove(record);
        record.setHealthProfile(null);
    }

    public void addHealthMetric(HealthMetrics metric) {
        healthMetrics.add(metric);
        metric.setHealthProfile(this);
    }

    public void removeHealthMetric(HealthMetrics metric) {
        healthMetrics.remove(metric);
        metric.setHealthProfile(null);
    }

    public void addDietPlan(DietPlan plan) {
        dietPlans.add(plan);
        plan.setHealthProfile(this);
    }

    public void removeDietPlan(DietPlan plan) {
        dietPlans.remove(plan);
        plan.setHealthProfile(null);
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