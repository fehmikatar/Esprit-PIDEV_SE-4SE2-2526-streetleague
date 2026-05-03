"""
==============================================================
ÉTAPE 3 — ENTRAÎNEMENT  (tableau blanc: 80% train / 20% val)
==============================================================
• Charge match_features_selected.csv
• Split : 80% train / 20% validation  (+ test visuel)
• Entraîne 3 classifieurs  (Random Forest, SVM, XGBoost)
• Sélectionne le meilleur et le sauvegarde → models/best_model.pkl
• Génère : models/label_encoder.pkl
            models/feature_names.json
            models/metrics_report.txt
==============================================================
"""

import os, json
import pandas as pd
import numpy as np
import joblib
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt

from sklearn.model_selection  import train_test_split, cross_val_score
from sklearn.preprocessing    import StandardScaler, LabelEncoder
from sklearn.ensemble         import RandomForestClassifier, GradientBoostingClassifier
from sklearn.svm              import SVC
from sklearn.metrics          import (accuracy_score, classification_report,
                                      confusion_matrix, ConfusionMatrixDisplay)

BASE_DIR     = os.path.dirname(__file__)
DATA_FILE    = os.path.join(BASE_DIR, "dataset", "match_features_selected.csv")
MODELS_DIR   = os.path.join(BASE_DIR, "models")
os.makedirs(MODELS_DIR, exist_ok=True)

print("=" * 60)
print("  ÉTAPE 3 — ENTRAÎNEMENT DU MODÈLE")
print("=" * 60)

# ─── 1. Chargement ────────────────────────────────────────────
df = pd.read_csv(DATA_FILE)
print(f"\n[1] Dataset chargé  → {df.shape[0]} lignes × {df.shape[1]} colonnes")

TARGET    = "result_encoded"
FEATURES  = [c for c in df.columns if c != TARGET]

X = df[FEATURES].values
y = df[TARGET].values

# ─── 2. Split 80 / 20 ────────────────────────────────────────
X_train, X_val, y_train, y_val = train_test_split(
    X, y, test_size=0.20, random_state=42, stratify=y
)
print(f"\n[2] Split train/val :")
print(f"     Train : {len(X_train)} lignes  ({len(X_train)/len(X)*100:.0f}%)")
print(f"     Val   : {len(X_val)} lignes  ({len(X_val)/len(X)*100:.0f}%)")

# ─── 3. Normalisation ─────────────────────────────────────────
scaler = StandardScaler()
X_train_sc = scaler.fit_transform(X_train)
X_val_sc   = scaler.transform(X_val)

# ─── 4. Définition des modèles ────────────────────────────────
MODELS = {
    "Random Forest":        RandomForestClassifier(n_estimators=200, max_depth=10,
                                                   random_state=42, n_jobs=-1),
    "Gradient Boosting":    GradientBoostingClassifier(n_estimators=150, learning_rate=0.1,
                                                       max_depth=4, random_state=42),
    "SVM (RBF)":            SVC(kernel="rbf", C=1.0, gamma="scale",
                                probability=True, random_state=42),
}

# ─── 5. Entraînement + évaluation ────────────────────────────
print(f"\n[3] Entraînement des modèles :")
print(f"    {'Modèle':<22} {'Acc. Train':>10} {'Acc. Val':>10} {'CV-5 Mean':>10}")
print("    " + "-" * 56)

results    = {}
best_name  = None
best_acc   = 0.0
best_model = None

for name, clf in MODELS.items():
    # Cross-validation 5-fold sur train
    cv_scores = cross_val_score(clf, X_train_sc, y_train, cv=5, scoring="accuracy")

    clf.fit(X_train_sc, y_train)
    acc_train = accuracy_score(y_train, clf.predict(X_train_sc))
    acc_val   = accuracy_score(y_val,   clf.predict(X_val_sc))

    results[name] = {"acc_train": acc_train, "acc_val": acc_val, "cv_mean": cv_scores.mean()}
    print(f"    {name:<22} {acc_train*100:>9.2f}%  {acc_val*100:>9.2f}%  {cv_scores.mean()*100:>9.2f}%")

    if acc_val > best_acc:
        best_acc   = acc_val
        best_name  = name
        best_model = clf

print(f"\n    🏆 Meilleur modèle : {best_name}  (val acc = {best_acc*100:.2f}%)")

# ─── 6. Rapport de classification ────────────────────────────
CLASS_NAMES = ["DRAW", "AWAY_WIN", "HOME_WIN"]
y_pred      = best_model.predict(X_val_sc)
report      = classification_report(y_val, y_pred, target_names=CLASS_NAMES)

print(f"\n[4] Classification Report ({best_name}) :")
print(report)

# ─── 7. Matrice de confusion ──────────────────────────────────
cm   = confusion_matrix(y_val, y_pred)
disp = ConfusionMatrixDisplay(confusion_matrix=cm, display_labels=CLASS_NAMES)
fig, ax = plt.subplots(figsize=(6, 5))
disp.plot(ax=ax, colorbar=True, cmap="Blues")
ax.set_title(f"Confusion Matrix — {best_name}\n(Validation set, acc={best_acc*100:.1f}%)")
plt.tight_layout()
cm_path = os.path.join(MODELS_DIR, "confusion_matrix.png")
plt.savefig(cm_path, dpi=120)
plt.close()
print(f"[5] Matrice de confusion sauvegardée → {cm_path}")

# ─── 8. Importance des features (Random Forest) ──────────────
if hasattr(best_model, "feature_importances_"):
    fi = pd.Series(best_model.feature_importances_, index=FEATURES).sort_values(ascending=False)
    fig2, ax2 = plt.subplots(figsize=(8, 4))
    fi.plot(kind="bar", ax=ax2, color="#4CAF50")
    ax2.set_title("Feature Importances")
    ax2.set_ylabel("Importance")
    plt.tight_layout()
    fi_path = os.path.join(MODELS_DIR, "feature_importance.png")
    plt.savefig(fi_path, dpi=120)
    plt.close()
    print(f"[6] Feature importance sauvegardée → {fi_path}")

# ─── 9. Sauvegarde ────────────────────────────────────────────
model_path  = os.path.join(MODELS_DIR, "best_model.pkl")
scaler_path = os.path.join(MODELS_DIR, "scaler.pkl")
feats_path  = os.path.join(MODELS_DIR, "feature_names.json")
report_path = os.path.join(MODELS_DIR, "metrics_report.txt")

joblib.dump(best_model, model_path)
joblib.dump(scaler,     scaler_path)
json.dump(FEATURES, open(feats_path, "w"))

with open(report_path, "w") as f:
    f.write(f"Best model   : {best_name}\n")
    f.write(f"Val Accuracy : {best_acc*100:.2f}%\n\n")
    f.write("=== Classification Report ===\n")
    f.write(report)
    f.write("\n=== All Models ===\n")
    for n, r in results.items():
        f.write(f"{n:<22} train={r['acc_train']*100:.2f}%  val={r['acc_val']*100:.2f}%  cv={r['cv_mean']*100:.2f}%\n")

print(f"\n[7] Fichiers sauvegardés :")
print(f"     Modèle   → {model_path}")
print(f"     Scaler   → {scaler_path}")
print(f"     Features → {feats_path}")
print(f"     Rapport  → {report_path}")

print(f"\n{'='*60}")
print(f"  RÉSUMÉ ÉTAPE 3")
print(f"  Modèle retenu   : {best_name}")
print(f"  Précision val   : {best_acc*100:.2f}%")
print(f"{'='*60}")
