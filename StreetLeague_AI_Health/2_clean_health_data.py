import pandas as pd
import os

def clean_data():
    input_path = 'healthdata/health_dataset.csv'
    output_path = 'healthdata/health_cleaned.csv'
    
    if not os.path.exists(input_path):
        print(f"File {input_path} not found.")
        return

    df = pd.read_csv(input_path, sep='\t')
    print("Initial Data Shape:", df.shape)

    # Map Muscle_Soreness text to integer (Aucune=1, Légère=2, Intense=3)
    soreness_mapping = {'Aucune': 1, 'Légère': 2, 'Intense': 3}
    if 'Muscle_Soreness' in df.columns:
        df['Muscle_Soreness'] = df['Muscle_Soreness'].map(soreness_mapping)
        # Handle possible unmapped values
        df['Muscle_Soreness'].fillna(2, inplace=True) 

    # Select the 12 features that match AthleteRequest in Spring Boot
    # 1. Duration -> Duration
    # 2. Intensity -> Intensity
    # 3. TrainingLoad -> Training_Load
    # 4. SleepHours -> Sleep_Hours
    # 5. SleepQuality -> Sleep_Quality
    # 6. FatigueLevel -> Fatigue_Level
    # 7. HydrationLiters -> Hydration_Liters
    # 8. BMI -> BMI
    # 9. WeightKg -> Weight_kg
    # 10. CaloriesIn -> Calories_In
    # 11. NutritionAdherence -> Nutrition_Adherence
    # 12. SorenessCode -> Muscle_Soreness
    # Target -> Health_Score
    
    selected_columns = [
        'Duration', 'Intensity', 'Training_Load', 'Sleep_Hours', 
        'Sleep_Quality', 'Fatigue_Level', 'Hydration_Liters', 
        'BMI', 'Weight_kg', 'Calories_In', 'Nutrition_Adherence', 
        'Muscle_Soreness', 'Health_Score'
    ]
    
    # Check if all columns exist
    missing_cols = [col for col in selected_columns if col not in df.columns]
    if missing_cols:
        print(f"Missing columns in dataset: {missing_cols}")
        return
        
    df_selected = df[selected_columns].copy()
    
    # Optional: ensure numeric types
    df_selected = df_selected.apply(pd.to_numeric, errors='coerce')
    df_selected.dropna(inplace=True)
    
    # Normalize Health_Score to be out of 100
    max_score = df_selected['Health_Score'].max()
    if max_score > 100:
        df_selected['Health_Score'] = (df_selected['Health_Score'] / max_score) * 100.0
        df_selected['Health_Score'] = df_selected['Health_Score'].round(2)
    
    df_selected.to_csv(output_path, index=False)
    print(f"Cleaned data saved to {output_path}. Shape: {df_selected.shape}")

if __name__ == "__main__":
    clean_data()
