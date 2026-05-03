import pandas as pd
import numpy as np
import os
import random

def generate_dataset():
    output_dir = 'healthdata'
    if not os.path.exists(output_dir):
        os.makedirs(output_dir)
        
    output_path = f"{output_dir}/health_dataset.csv"
    
    # Generate 100 rows of fake health data
    data = []
    sports = ['Boxe', 'Course', 'Football', 'Volleyball', 'Squash', 'Tennis', 'Musculation', 'Natation']
    soreness = ['Aucune', 'Légère', 'Intense']
    
    for i in range(100):
        duration = random.randint(30, 150)
        intensity = random.randint(1, 10)
        row = {
            'Date': f"{random.randint(1,28)}/1/2026",
            'Sport': random.choice(sports),
            'Duration': duration,
            'Intensity': intensity,
            'Training_Load': duration * intensity,
            'Sleep_Hours': random.randint(4, 10),
            'Sleep_Quality': random.randint(1, 5),
            'Fatigue_Level': random.randint(1, 5),
            'Hydration_Liters': round(random.uniform(1.5, 4.5), 1),
            'Muscle_Soreness': random.choice(soreness),
            'Injury_Risk': random.randint(0, 2),
            'BMI': round(random.uniform(18.5, 30.0), 2),
            'Weight_kg': random.randint(60, 110),
            'Calories_In': random.randint(1800, 4000),
            'Nutrition_Adherence': random.randint(1, 5),
            'Health_Score': random.randint(3000, 50000)
        }
        data.append(row)
        
    df = pd.DataFrame(data)
    df.to_csv(output_path, sep='\t', index=False)
    print(f"Generated fake dataset at {output_path}")

if __name__ == "__main__":
    generate_dataset()
