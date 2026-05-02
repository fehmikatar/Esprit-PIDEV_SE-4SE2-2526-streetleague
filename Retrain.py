"""
====================================================
  STREETLEAGUE ML — Script de ré-entraînement
  Fichier : retrain.py
  Lancer  : python retrain.py
====================================================

Adapté pour StreetLeague :
  - Cible     : xp_per_day
  - Features  : athlete_level, sport_type, match_score,
                teamwork_score, session_duration, calories_burned,
                win_rate, streak_days, recovery_score
  - Encodeurs : le_sport.pkl + le_badge.pkl
  - Seuil MAE : 80 XP/jour
"""

import numpy as np
import pandas as pd
import joblib
import datetime
import os
from sklearn.ensemble        import RandomForestRegressor
from sklearn.preprocessing   import StandardScaler, LabelEncoder
from sklearn.model_selection import train_test_split
from sklearn.metrics         import mean_absolute_error, r2_score


# ============================================================
# CONFIGURATION
# ============================================================
MAE_SEUIL  = 80.0
MODELS_DIR = "models"

FEATURES = [
    'athlete_level', 'total_xp', 'sport_type_enc', 'badge_type_enc',
    'match_score', 'teamwork_score', 'session_duration',
    'calories_burned', 'win_rate', 'streak_days', 'recovery_score',
    'xp_objective', 'duration_days', 'xp_gap',
    'xp_needed_per_day', 'score_per_min', 'activity_index',
    'urgency', 'level_xp_ratio', 'session_efficiency',
    'health_score', 'difficulty'
]
TARGET = 'xp_per_day'


# ============================================================
# ÉTAPE 1 — Charger les données StreetLeague
# ============================================================
def load_all_data():
    """
    En production : charge depuis MySQL
        SELECT * FROM player_level JOIN performance USING(player_id)

    Ici : génère les données StreetLeague comme dans le notebook.
    """
    print("📂 Chargement des données StreetLeague...")

    N = 12000
    np.random.seed(42)

    # ── Sport type (remplace player_type) ──────────────────────────
    sport_type = np.random.choice(
        ['football', 'basket', 'tennis', 'padel', 'running'],
        size=N, p=[0.35, 0.25, 0.15, 0.15, 0.10]
    )

    # ── Niveau athlète (1-50) selon le sport ───────────────────────
    level_map = {
        'football': (1, 40), 'basket': (1, 40),
        'tennis'  : (1, 50), 'padel' : (1, 35),
        'running' : (1, 45)
    }
    athlete_level = np.array([
        np.random.randint(*level_map[s]) for s in sport_type
    ])

    # ── XP total ───────────────────────────────────────────────────
    xp_per_level = np.random.randint(400, 700, size=N)
    total_xp     = athlete_level * xp_per_level + np.random.randint(0, 500, N)

    # ── Performance selon le sport ─────────────────────────────────
    score_map = {
        'football': (300, 2000), 'basket': (400, 2500),
        'tennis'  : (200, 1800), 'padel' : (250, 1500),
        'running' : (100, 1200)
    }
    match_score = np.array([
        np.random.randint(*score_map[s]) for s in sport_type
    ]).astype(float)

    teamwork_score   = np.random.randint(0, 25, N).astype(float)
    session_duration = np.random.randint(20, 150, N).astype(float)
    calories_burned  = (session_duration * 6 + np.random.normal(0, 30, N)).clip(100, 1200)
    win_rate         = np.random.beta(2, 3, N).round(2)
    streak_days      = np.random.geometric(p=0.15, size=N).clip(1, 60)
    recovery_score   = np.random.uniform(3, 10, N).round(1)

    # ── Objectifs ──────────────────────────────────────────────────
    xp_objective  = total_xp + np.random.randint(2000, 15000, N)
    duration_days = np.random.choice([7,14,21,30,60], size=N,
                                     p=[0.1,0.2,0.2,0.4,0.1]).astype(float)

    # ── Badge type ─────────────────────────────────────────────────
    badge_type = np.where(
        athlete_level <= 10, 'bronze',
        np.where(athlete_level <= 25, 'silver',
        np.where(athlete_level <= 40, 'gold', 'elite'))
    )

    # ── Encodage ───────────────────────────────────────────────────
    le_sport = LabelEncoder()
    le_badge = LabelEncoder()
    sport_enc = le_sport.fit_transform(sport_type)
    badge_enc = le_badge.fit_transform(badge_type)

    # ── Features dérivées ──────────────────────────────────────────
    xp_gap          = xp_objective - total_xp
    score_per_min   = match_score / np.maximum(session_duration, 1)
    xp_needed_pd    = xp_gap / np.maximum(duration_days, 1)
    level_xp_ratio  = athlete_level / np.maximum(total_xp, 1) * 1000
    activity_index  = (match_score*0.4 + teamwork_score*20
                       + session_duration*2 + streak_days*10)
    urgency         = xp_gap / np.maximum(duration_days, 1)
    session_eff     = match_score / np.maximum(session_duration, 1)
    health_sc       = recovery_score*50 + calories_burned*0.1 + win_rate*100

    raw_diff   = xp_needed_pd / 200 + (athlete_level / 50) * 2
    difficulty = np.clip(
        np.round(raw_diff + np.random.normal(0, 0.3, N)), 1, 5
    ).astype(int)

    # ── Cible xp_per_day ───────────────────────────────────────────
    noise      = np.random.normal(0, 25, N)
    xp_per_day = (
        xp_needed_pd    * 0.85
        + score_per_min * 8
        + teamwork_score* 15
        + (session_duration / 60) * 20
        + streak_days   * 5
        + calories_burned * 0.05
        + noise
    ).clip(30, 2000)

    # ── DataFrame final ────────────────────────────────────────────
    df = pd.DataFrame({
        'athlete_level'     : athlete_level,
        'total_xp'          : total_xp,
        'sport_type_enc'    : sport_enc,
        'badge_type_enc'    : badge_enc,
        'match_score'       : match_score.round(1),
        'teamwork_score'    : teamwork_score,
        'session_duration'  : session_duration,
        'calories_burned'   : calories_burned.round(1),
        'win_rate'          : win_rate,
        'streak_days'       : streak_days,
        'recovery_score'    : recovery_score,
        'xp_objective'      : xp_objective,
        'duration_days'     : duration_days,
        'xp_gap'            : xp_gap,
        'xp_needed_per_day' : xp_needed_pd.round(2),
        'score_per_min'     : score_per_min.round(2),
        'activity_index'    : activity_index.round(2),
        'urgency'           : urgency.round(2),
        'level_xp_ratio'    : level_xp_ratio.round(4),
        'session_efficiency': session_eff.round(2),
        'health_score'      : health_sc.round(2),
        'difficulty'        : difficulty,
        'xp_per_day'        : xp_per_day.round(1),
    })

    # Supprimer outliers
    Q1 = df['xp_per_day'].quantile(0.01)
    Q3 = df['xp_per_day'].quantile(0.99)
    df = df[(df['xp_per_day'] >= Q1) & (df['xp_per_day'] <= Q3)]

    print(f"   ✅ {len(df):,} lignes chargées")
    return df, le_sport, le_badge


# ============================================================
# ÉTAPE 2 — Monitoring du modèle actuel
# ============================================================
def check_current_model(X_val, y_val):
    """
    Mesure la MAE du modèle actuellement en production.
    Si MAE > MAE_SEUIL → modèle dégradé → ré-entraîner.
    """
    model_path = os.path.join(MODELS_DIR, "best_model.pkl")

    if not os.path.exists(model_path):
        print("⚠️  Aucun modèle existant → premier entraînement.")
        return None, float('inf')

    model      = joblib.load(model_path)
    y_pred     = model.predict(X_val)
    mae_actuel = mean_absolute_error(y_val, y_pred)

    print(f"📊 Modèle actuel — MAE = {mae_actuel:.2f} XP/jour")
    if mae_actuel > MAE_SEUIL:
        print(f"   🔴 Dégradé ! MAE {mae_actuel:.1f} > seuil {MAE_SEUIL}")
    else:
        print(f"   🟢 Modèle OK. MAE {mae_actuel:.1f} ≤ seuil {MAE_SEUIL}")

    return model, mae_actuel


# ============================================================
# ÉTAPE 3 — Ré-entraîner le modèle
# ============================================================
def retrain(df):
    """
    Ré-entraîne RandomForest sur toutes les données disponibles.
    """
    print("\n🔁 Ré-entraînement du modèle StreetLeague...")

    X = df[FEATURES]
    y = df[TARGET]

    X_train, X_val, y_train, y_val = train_test_split(
        X, y, test_size=0.2, random_state=42
    )

    nouveau_model = RandomForestRegressor(
        n_estimators=200, max_depth=12,
        n_jobs=-1, random_state=42
    )
    nouveau_model.fit(X_train, y_train)

    mae_nouveau = mean_absolute_error(y_val, nouveau_model.predict(X_val))
    r2_nouveau  = r2_score(y_val, nouveau_model.predict(X_val))

    print(f"   ✅ Nouveau modèle — MAE = {mae_nouveau:.2f} XP/jour | R² = {r2_nouveau:.4f}")

    return nouveau_model, mae_nouveau, X_val, y_val


# ============================================================
# ÉTAPE 4 — Valider et déployer si meilleur
# ============================================================
def validate_and_deploy(nouveau_model, mae_nouveau, mae_actuel, le_sport, le_badge):
    """
    Déploie le nouveau modèle SEULEMENT s'il est meilleur que l'actuel.
    Archive toujours une copie horodatée.
    Sauvegarde le_sport.pkl et le_badge.pkl (nouveau système StreetLeague).
    """
    os.makedirs(MODELS_DIR, exist_ok=True)

    # Archive horodatée
    ts           = datetime.datetime.now().strftime('%Y%m%d_%H%M%S')
    archive_path = os.path.join(MODELS_DIR, f"model_{ts}.pkl")
    joblib.dump(nouveau_model, archive_path)
    print(f"\n💾 Archivé : {archive_path}")

    if mae_nouveau < mae_actuel:
        # ── Déployer les nouveaux fichiers StreetLeague ────────────
        joblib.dump(nouveau_model,
                    os.path.join(MODELS_DIR, "best_model.pkl"))
        joblib.dump(le_sport,
                    os.path.join(MODELS_DIR, "le_sport.pkl"))  # ← nouveau nom
        joblib.dump(le_badge,
                    os.path.join(MODELS_DIR, "le_badge.pkl"))  # ← nouveau nom

        print(f"🚀 DÉPLOYÉ !")
        print(f"   MAE {mae_nouveau:.2f} < {mae_actuel:.2f} XP/jour")
        print(f"   → best_model.pkl mis à jour")
        print(f"   → le_sport.pkl mis à jour")
        print(f"   → le_badge.pkl mis à jour")
        print(f"   → FastAPI recharge automatiquement via --reload")
        return True
    else:
        print(f"⏸️  Pas de déploiement.")
        print(f"   Nouveau MAE {mae_nouveau:.2f} ≥ Actuel {mae_actuel:.2f} XP/jour")
        return False


# ============================================================
# PIPELINE COMPLET
# ============================================================
def run_pipeline():
    print("=" * 55)
    print("  STREETLEAGUE ML — Pipeline de ré-entraînement")
    print("=" * 55)
    print()

    # Étape 1 : charger les données
    df, le_sport, le_badge = load_all_data()

    # Préparer jeu de validation pour le monitoring
    X_val_monitor = df[FEATURES].sample(2000, random_state=99)
    y_val_monitor = df.loc[X_val_monitor.index, TARGET]

    # Étape 2 : vérifier le modèle actuel
    print("\n📡 Vérification du modèle actuel...")
    _, mae_actuel = check_current_model(X_val_monitor, y_val_monitor)

    # Étape 3 : ré-entraîner
    nouveau_model, mae_nouveau, _, _ = retrain(df)

    # Étape 4 : valider et déployer
    deploye = validate_and_deploy(
        nouveau_model, mae_nouveau, mae_actuel,
        le_sport, le_badge           # ← nouveau : 2 encodeurs séparés
    )

    print()
    print("=" * 55)
    print(f"  Résultat  : {'✅ Modèle mis à jour' if deploye else '⏸️  Modèle inchangé'}")
    print(f"  MAE avant : {mae_actuel:.2f} XP/jour")
    print(f"  MAE après : {mae_nouveau:.2f} XP/jour")
    print("=" * 55)


if __name__ == '__main__':
    run_pipeline()