"""
Script utilitaire — exécute les 4 étapes du pipeline IA dans l'ordre.
Usage : python run_all.py
"""
import subprocess, sys, os

BASE = os.path.dirname(__file__)
STEPS = [
    ("1 — Génération du dataset",    "1_generate_dataset.py"),
    ("2 — Nettoyage des données",     "2_clean_data.py"),
    ("3 — Entraînement du modèle",    "3_train_model.py"),
]

def run(label, script):
    print(f"\n{'='*60}")
    print(f"  ▶  {label}")
    print(f"{'='*60}")
    result = subprocess.run(
        [sys.executable, os.path.join(BASE, script)],
        cwd=BASE
    )
    if result.returncode != 0:
        print(f"\n❌  Erreur dans {script}. Pipeline arrêté.")
        sys.exit(1)

for label, script in STEPS:
    run(label, script)

print(f"\n{'='*60}")
print("  ✅  Pipeline terminé !")
print(f"{'='*60}")
print("\n  Pour démarrer l'API de prédiction :")
print("     uvicorn 4_predict_api:app --host 0.0.0.0 --port 8000 --reload")
print("\n  Swagger UI : http://localhost:8000/docs")
