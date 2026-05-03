"""
==============================================================
ÉTAPE 2 — NETTOYAGE  (tableau blanc: Nettoyage → 10 features)
==============================================================
• Supprime les valeurs manquantes / aberrantes
• Encode les variables catégorielles
• Sélectionne les 10 meilleures features (feature selection)
• Exporte : dataset/match_cleaned.csv
             dataset/match_features_selected.csv
==============================================================
"""

import pandas as pd
import numpy as np
import os
from sklearn.preprocessing import LabelEncoder
from sklearn.feature_selection import SelectKBest, chi2, f_classif

BASE_DIR   = os.path.dirname(__file__)
INPUT_FILE = os.path.join(BASE_DIR, "dataset", "match_dataset.csv")
OUT_CLEAN  = os.path.join(BASE_DIR, "dataset", "match_cleaned.csv")
OUT_SEL    = os.path.join(BASE_DIR, "dataset", "match_features_selected.csv")

print("=" * 60)
print("  ÉTAPE 2 — NETTOYAGE DU DATASET")
print("=" * 60)

# ─── 1. Chargement ────────────────────────────────────────────
df = pd.read_csv(INPUT_FILE)
print(f"\n[1] Données brutes  → {df.shape[0]} lignes × {df.shape[1]} colonnes")

# ─── 2. Valeurs manquantes ────────────────────────────────────
print(f"\n[2] Valeurs manquantes par colonne :")
missing = df.isnull().sum()
if missing.sum() == 0:
    print("     ✅  Aucune valeur manquante")
else:
    print(missing[missing > 0])
    # Imputation : numériques → médiane, catégoriques → mode
    for col in df.columns:
        if df[col].isnull().any():
            if df[col].dtype == object:
                df[col].fillna(df[col].mode()[0], inplace=True)
            else:
                df[col].fillna(df[col].median(), inplace=True)

# ─── 3. Valeurs aberrantes (méthode IQR) ──────────────────────
numeric_cols = [
    "home_goals_avg", "away_goals_avg",
    "home_conceded_avg", "away_conceded_avg",
    "home_yellow_avg", "away_yellow_avg",
    "home_red_avg", "away_red_avg",
    "home_fouls_avg", "away_fouls_avg",
    "home_rating_avg", "away_rating_avg",
]

print(f"\n[3] Détection des valeurs aberrantes (IQR) :")
total_outliers = 0
for col in numeric_cols:
    Q1  = df[col].quantile(0.25)
    Q3  = df[col].quantile(0.75)
    IQR = Q3 - Q1
    lo  = Q1 - 1.5 * IQR
    hi  = Q3 + 1.5 * IQR
    n_out = ((df[col] < lo) | (df[col] > hi)).sum()
    total_outliers += n_out
    # Capping (Winsorisation)
    df[col] = df[col].clip(lower=lo, upper=hi)

print(f"     {total_outliers} valeur(s) aberrante(s) corrigées par Winsorisation")

# ─── 4. Encodage ──────────────────────────────────────────────
print(f"\n[4] Encodage des variables catégorielles :")
le = LabelEncoder()
df["result_encoded"] = le.fit_transform(df["result"])     # DRAW=0 AWAY_WIN=1 HOME_WIN=2
classes = dict(zip(le.classes_, le.transform(le.classes_)))
print(f"     result → {classes}")

# competition_format déjà numérique (0/1/2)
print(f"     competition_format  → déjà numérique")
print(f"     is_neutral_venue    → déjà binaire")

# ─── 5. Colonnes identifiants à supprimer ────────────────────
print(f"\n[5] Suppression des identifiants non-informants :")
id_cols = ["match_id", "competition_id", "home_team_id", "away_team_id",
           "home_score", "away_score", "result"]
df_clean = df.drop(columns=id_cols)
print(f"     Colonnes supprimées : {id_cols}")
print(f"     Forme après nettoyage : {df_clean.shape}")

df_clean.to_csv(OUT_CLEAN, index=False)
print(f"\n✅ Fichier nettoyé sauvegardé → {OUT_CLEAN}")

# ─── 6. Sélection des 10 meilleures features ─────────────────
print(f"\n[6] Sélection des 10 meilleures features (ANOVA f_classif) :")

feature_cols = [c for c in df_clean.columns if c != "result_encoded"]
X = df_clean[feature_cols].values
y = df_clean["result_encoded"].values

selector = SelectKBest(score_func=f_classif, k=10)
selector.fit(X, y)

scores  = pd.Series(selector.scores_, index=feature_cols).sort_values(ascending=False)
top10   = scores.head(10).index.tolist()

print(f"\n     Score F par feature :")
for feat, score in scores.items():
    marker = "  ← ✅" if feat in top10 else ""
    print(f"     {feat:<25} {score:>8.2f}{marker}")

df_selected = df_clean[top10 + ["result_encoded"]]
df_selected.to_csv(OUT_SEL, index=False)

print(f"\n✅ Features sélectionnées sauvegardées → {OUT_SEL}")
print(f"\n{'='*60}")
print(f"  RÉSUMÉ ÉTAPE 2")
print(f"  Lignes finales      : {len(df_selected)}")
print(f"  Features retenues   : {len(top10)}")
print(f"  Target classes      : {classes}")
print(f"{'='*60}")
