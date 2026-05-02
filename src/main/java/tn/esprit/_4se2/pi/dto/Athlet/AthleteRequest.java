package tn.esprit._4se2.pi.dto.Athlet;

public class AthleteRequest {
    private double duration;
    private int intensity;
    private double trainingLoad;
    private double sleepHours;
    private int sleepQuality;
    private int fatigueLevel;
    private double hydrationLiters;
    private double bmi;
    private double weightKg;
    private double caloriesIn;
    private int nutritionAdherence;
    private int sorenessCode;

    // Getters et setters (obligatoires)
    public double getDuration() { return duration; }
    public void setDuration(double duration) { this.duration = duration; }
    public int getIntensity() { return intensity; }
    public void setIntensity(int intensity) { this.intensity = intensity; }
    public double getTrainingLoad() { return trainingLoad; }
    public void setTrainingLoad(double trainingLoad) { this.trainingLoad = trainingLoad; }
    public double getSleepHours() { return sleepHours; }
    public void setSleepHours(double sleepHours) { this.sleepHours = sleepHours; }
    public int getSleepQuality() { return sleepQuality; }
    public void setSleepQuality(int sleepQuality) { this.sleepQuality = sleepQuality; }
    public int getFatigueLevel() { return fatigueLevel; }
    public void setFatigueLevel(int fatigueLevel) { this.fatigueLevel = fatigueLevel; }
    public double getHydrationLiters() { return hydrationLiters; }
    public void setHydrationLiters(double hydrationLiters) { this.hydrationLiters = hydrationLiters; }
    public double getBmi() { return bmi; }
    public void setBmi(double bmi) { this.bmi = bmi; }
    public double getWeightKg() { return weightKg; }
    public void setWeightKg(double weightKg) { this.weightKg = weightKg; }
    public double getCaloriesIn() { return caloriesIn; }
    public void setCaloriesIn(double caloriesIn) { this.caloriesIn = caloriesIn; }
    public int getNutritionAdherence() { return nutritionAdherence; }
    public void setNutritionAdherence(int nutritionAdherence) { this.nutritionAdherence = nutritionAdherence; }
    public int getSorenessCode() { return sorenessCode; }
    public void setSorenessCode(int sorenessCode) { this.sorenessCode = sorenessCode; }
}