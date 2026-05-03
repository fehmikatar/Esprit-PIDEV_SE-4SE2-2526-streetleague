import pandas as pd
from sklearn.model_selection import train_test_split
from sklearn.ensemble import RandomForestRegressor
from sklearn.metrics import mean_squared_error, r2_score
import joblib
import os

def train_model():
    input_path = 'healthdata/health_cleaned.csv'
    model_output_path = 'modelhealth/health_score_model.pkl'
    
    if not os.path.exists(input_path):
        print(f"File {input_path} not found. Please run 2_clean_health_data.py first.")
        return

    # Load cleaned data
    df = pd.read_csv(input_path)
    
    # Apply the same logic as Spring Boot: Calories should be divided by 1000 to match scale
    df['Calories_In'] = df['Calories_In'] / 1000.0

    X = df.drop('Health_Score', axis=1)
    y = df['Health_Score']
    
    # Split data
    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)
    
    print("Training Random Forest Regressor...")
    model = RandomForestRegressor(n_estimators=100, random_state=42)
    model.fit(X_train, y_train)
    
    # Evaluate
    predictions = model.predict(X_test)
    mse = mean_squared_error(y_test, predictions)
    r2 = r2_score(y_test, predictions)
    
    print(f"Model Evaluation -> MSE: {mse:.2f}, R2 Score: {r2:.4f}")
    
    # Save model
    joblib.dump(model, model_output_path)
    print(f"Model successfully saved to {model_output_path}")
    print("Features used during training in order:")
    print(list(X.columns))

if __name__ == "__main__":
    train_model()
