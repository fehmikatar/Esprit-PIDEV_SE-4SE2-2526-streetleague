from flask import Flask, request, jsonify
import joblib
import pandas as pd
import numpy as np

app = Flask(__name__)

# Charger le modèle au démarrage
model_path = 'modelhealth/health_score_model.pkl'
try:
    model = joblib.load(model_path)
    print(f"Modèle {model_path} chargé avec succès !")
except Exception as e:
    model = None
    print(f"Erreur lors du chargement du modèle : {e}")

@app.route('/predict', methods=['POST'])
def predict():
    if model is None:
        return jsonify({"error": "Le modèle n'est pas chargé. Avez-vous lancé 3_train_health_model.py ?"}), 500
        
    try:
        data = request.get_json()
        
        # S'attendre à une liste de features ou un objet JSON
        if isinstance(data, list):
            features = data
        else:
            # Ordre des features : Duration, Intensity, Training_Load, Sleep_Hours, Sleep_Quality, 
            # Fatigue_Level, Hydration_Liters, BMI, Weight_kg, Calories_In, Nutrition_Adherence, Muscle_Soreness
            features = [
                data.get('Duration', 60),
                data.get('Intensity', 5),
                data.get('Training_Load', data.get('Duration', 60) * data.get('Intensity', 5)),
                data.get('Sleep_Hours', 7),
                data.get('Sleep_Quality', 4),
                data.get('Fatigue_Level', 2),
                data.get('Hydration_Liters', 2.2),
                data.get('BMI', 22.5),
                data.get('Weight_kg', 75.0),
                data.get('Calories_In', 2500) / 1000.0, # Normalisation comme dans Spring Boot
                data.get('Nutrition_Adherence', 3),
                data.get('Muscle_Soreness', 2)
            ]
            
        features_array = np.array(features).reshape(1, -1)
        prediction = model.predict(features_array)
        
        # Limiter le score entre 0 et 100
        score = max(0, min(100, round(float(prediction[0]))))
        
        return jsonify({"health_score": score})
        
    except Exception as e:
        return jsonify({"error": str(e)}), 400

if __name__ == '__main__':
    print("Démarrage de l'API Health Predictor sur le port 5000...")
    app.run(host='0.0.0.0', port=5000)
