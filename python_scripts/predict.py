import sys
import joblib
import json
import os
import numpy as np

def predict():
    try:
        # Determine which model to use (default to health_score_model.pkl)
        model_name = "health_score_model.pkl"
        if len(sys.argv) > 2:
            model_name = sys.argv[2]
            
        model_path = os.path.join(os.path.dirname(__file__), '..', 'src', 'main', 'resources', 'modelhealth', model_name)
        
        if not os.path.exists(model_path):
            print(json.dumps({"error": f"Model file not found at {model_path}"}))
            return

        model = joblib.load(model_path)
        
        # Get input features from argument (JSON array)
        if len(sys.argv) > 1:
            features = json.loads(sys.argv[1])
            # Reshape for prediction
            features_array = np.array(features).reshape(1, -1)
            prediction = model.predict(features_array)
            
            print(json.dumps({"prediction": float(prediction[0])}))
        else:
            print(json.dumps({"error": "No features provided"}))
            
    except Exception as e:
        print(json.dumps({"error": str(e)}))

if __name__ == "__main__":
    predict()
