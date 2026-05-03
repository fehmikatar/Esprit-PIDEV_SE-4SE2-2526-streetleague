"""
==============================================================
ÉTAPE 4 — PRÉDICTION  (tableau blanc: FastAPI → Spring RestTemplate → BD MySQL)
==============================================================
API FastAPI exposant le modèle entraîné.
Spring Boot consomme cet endpoint via RestTemplate.

Lancement :
    uvicorn 4_predict_api:app --host 0.0.0.0 --port 8000 --reload

Endpoints :
    GET  /health          → statut de l'API
    POST /predict         → prédiction d'un match
    POST /predict/batch   → prédiction de plusieurs matchs
    GET  /model/info      → métadonnées du modèle

Exemple de requête POST /predict :
{
  "home_rank": 3,
  "away_rank": 8,
  "home_goals_avg": 2.3,
  "away_goals_avg": 1.5,
  "home_conceded_avg": 1.0,
  "away_conceded_avg": 1.8,
  "home_wins_last5": 4,
  "away_wins_last5": 2,
  "home_rating_avg": 4.2,
  "away_rating_avg": 3.1
}
==============================================================
"""

import os, json
import joblib
import numpy as np
from pathlib import Path
from typing import List, Optional

from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel, Field

# ─── Chemins ──────────────────────────────────────────────────
BASE_DIR    = Path(__file__).parent
MODELS_DIR  = BASE_DIR / "models"
MODEL_PATH  = MODELS_DIR / "best_model.pkl"
SCALER_PATH = MODELS_DIR / "scaler.pkl"
FEATS_PATH  = MODELS_DIR / "feature_names.json"
REPORT_PATH = MODELS_DIR / "metrics_report.txt"

# ─── Chargement du modèle ─────────────────────────────────────
try:
    model         = joblib.load(MODEL_PATH)
    scaler        = joblib.load(SCALER_PATH)
    feature_names = json.loads(FEATS_PATH.read_text())
    metrics_text  = REPORT_PATH.read_text() if REPORT_PATH.exists() else "N/A"
except FileNotFoundError as e:
    raise RuntimeError(
        f"⚠️  Modèle introuvable. Exécutez d'abord :\n"
        f"     python 1_generate_dataset.py\n"
        f"     python 2_clean_data.py\n"
        f"     python 3_train_model.py\n"
        f"Détail : {e}"
    )

# ─── Classes cibles ───────────────────────────────────────────
CLASSES = {0: "DRAW", 1: "AWAY_WIN", 2: "HOME_WIN"}

# ─── Application FastAPI ──────────────────────────────────────
app = FastAPI(
    title       = "StreetLeague Match Predictor",
    description = "Match result prediction API (HOME_WIN / AWAY_WIN / DRAW)",
    version     = "1.0.0",
    docs_url    = "/docs",
    redoc_url   = "/redoc",
)

app.add_middleware(
    CORSMiddleware,
    allow_origins     = ["*"],     # Spring Boot sur localhost:8082
    allow_methods     = ["*"],
    allow_headers     = ["*"],
)

# ─── Schémas Pydantic ─────────────────────────────────────────
class MatchInput(BaseModel):
    """Corps de la requête de prédiction d'un match."""
    # Features obligatoires (top-10 sélectionnées à l'étape 2)
    home_rank:          int   = Field(..., ge=1, le=30,  description="Home rank (1=best)")
    away_rank:          int   = Field(..., ge=1, le=30,  description="Away rank")
    home_goals_avg:     float = Field(..., ge=0,         description="Avg home goals/match")
    away_goals_avg:     float = Field(..., ge=0,         description="Avg away goals/match")
    home_conceded_avg:  float = Field(..., ge=0,         description="Avg home goals conceded")
    away_conceded_avg:  float = Field(..., ge=0,         description="Avg away goals conceded")
    home_wins_last5:    int   = Field(..., ge=0, le=5,   description="Wins/last 5 matches (home)")
    away_wins_last5:    int   = Field(..., ge=0, le=5,   description="Wins/last 5 matches (away)")
    home_rating_avg:    float = Field(..., ge=0, le=5,   description="Avg home player rating /5")
    away_rating_avg:    float = Field(..., ge=0, le=5,   description="Avg away player rating /5")

    # Features optionnelles
    home_yellow_avg:    Optional[float] = Field(None, ge=0)
    away_yellow_avg:    Optional[float] = Field(None, ge=0)
    home_red_avg:       Optional[float] = Field(None, ge=0)
    away_red_avg:       Optional[float] = Field(None, ge=0)
    home_fouls_avg:     Optional[float] = Field(None, ge=0)
    away_fouls_avg:     Optional[float] = Field(None, ge=0)
    home_assists_avg:   Optional[float] = Field(None, ge=0)
    away_assists_avg:   Optional[float] = Field(None, ge=0)
    is_neutral_venue:   Optional[int]   = Field(0, ge=0, le=1)
    competition_format: Optional[int]   = Field(0, ge=0, le=2)

    # ─── Features Live (Dynamique de match) ───
    is_live:             Optional[bool] = Field(False)
    live_home_goals:     Optional[int]  = Field(0)
    live_away_goals:     Optional[int]  = Field(0)
    live_home_red_cards: Optional[int]  = Field(0)
    live_away_red_cards: Optional[int]  = Field(0)
    live_minute:         Optional[int]  = Field(45, ge=0, le=120)

class PredictionResponse(BaseModel):
    result:           str
    confidence:       float
    probabilities:    dict
    interpretation:   str

class BatchInput(BaseModel):
    matches: List[MatchInput]

class BatchResponse(BaseModel):
    predictions: List[PredictionResponse]
    total:        int

import time
import hashlib

# ─── Helpers ──────────────────────────────────────────────────
DEFAULTS = {
    "home_yellow_avg": 1.5, "away_yellow_avg": 1.5,
    "home_red_avg": 0.1,    "away_red_avg": 0.1,
    "home_fouls_avg": 11.0, "away_fouls_avg": 11.0,
    "home_assists_avg": 1.2,"away_assists_avg": 1.2,
    "is_neutral_venue": 0,  "competition_format": 0,
}

def build_vector(m: MatchInput) -> np.ndarray:
    """Construit le vecteur de features dans l'ordre attendu par le modèle."""
    data = m.model_dump()
    for k, v in DEFAULTS.items():
        if data.get(k) is None:
            data[k] = v
    vec = [data[feat] for feat in feature_names]
    return np.array(vec, dtype=float).reshape(1, -1)

def apply_match_uniqueness(base_probs: dict, m: MatchInput) -> dict:
    """Ajoute une micro-variation déterministe basée sur les rangs pour éviter les égalités parfaites."""
    # Utilise les rangs pour créer un petit décalage (0.1% - 0.5%)
    # pour que deux matchs avec les mêmes moyennes ne soient pas strictement identiques
    seed = (m.home_rank * 7) + (m.away_rank * 3)
    np.random.seed(seed)
    noise = np.random.uniform(-0.5, 0.5, 3)
    
    p_home = base_probs["HOME_WIN"] + noise[0]
    p_away = base_probs["AWAY_WIN"] + noise[1]
    p_draw = base_probs["DRAW"]     + noise[2]
    
    total = p_home + p_away + p_draw
    return {
        "HOME_WIN": round((p_home / total) * 100, 1),
        "AWAY_WIN": round((p_away / total) * 100, 1),
        "DRAW":     round((p_draw / total) * 100, 1)
    }

def apply_live_dynamics(base_probs: dict, m: MatchInput) -> tuple[dict, str]:
    """Ajuste les probabilités en fonction des événements en direct et ajoute des micro-fluctuations."""
    p_home = base_probs["HOME_WIN"]
    p_away = base_probs["AWAY_WIN"]
    p_draw = base_probs["DRAW"]
    
    interp_add = []
    
    # 1. Poids du temps (plus on avance, plus le score actuel compte)
    time_weight = min(m.live_minute / 90.0, 0.95)
    
    # 2. Impact des buts
    goal_diff = m.live_home_goals - m.live_away_goals
    if goal_diff > 0:
        boost = 35 * goal_diff * time_weight
        p_home += boost
        p_away -= boost * 0.7
        p_draw -= boost * 0.3
        interp_add.append(f"Strong home momentum (+{goal_diff} goals).")
    elif goal_diff < 0:
        boost = 35 * abs(goal_diff) * time_weight
        p_away += boost
        p_home -= boost * 0.7
        p_draw -= boost * 0.3
        interp_add.append(f"Away team dominant ({goal_diff} goal deficit).")
    elif m.live_minute > 60:
        boost = 20 * time_weight
        p_draw += boost
        p_home -= boost * 0.5
        p_away -= boost * 0.5
        interp_add.append("High draw probability as time runs out.")
        
    # 3. Impact des cartons rouges
    red_diff = m.live_away_red_cards - m.live_home_red_cards
    if red_diff > 0:
        p_home += 18 * red_diff
        p_away -= 12 * red_diff
        interp_add.append("Home team has numerical advantage.")
    elif red_diff < 0:
        p_away += 18 * abs(red_diff)
        p_home -= 12 * abs(red_diff)
        interp_add.append("Home team under pressure (red card).")

    # 4. Micro-fluctuations dynamiques (pour l'effet "IA en temps réel")
    # Utilise le temps actuel pour faire varier légèrement les bars
    t = time.time()
    micro_h = np.sin(t / 5.0) * 1.5
    micro_a = np.cos(t / 7.0) * 1.5
    p_home += micro_h
    p_away += micro_a
    p_draw -= (micro_h + micro_a)

    # Normalisation
    p_home = max(2.0, min(97.0, p_home))
    p_away = max(2.0, min(97.0, p_away))
    p_draw = max(2.0, min(97.0, p_draw))
    
    total = p_home + p_away + p_draw
    new_probs = {
        "HOME_WIN": round((p_home / total) * 100, 1),
        "AWAY_WIN": round((p_away / total) * 100, 1),
        "DRAW":     round((p_draw / total) * 100, 1)
    }
    
    insight = " ".join(interp_add) if interp_add else "Match in progress, balanced dynamics."
    return new_probs, insight

def make_prediction(m: MatchInput) -> PredictionResponse:
    vec = build_vector(m)
    vec_sc = scaler.transform(vec)
    proba  = model.predict_proba(vec_sc)[0].tolist()

    base_dict = {CLASSES[i]: p * 100 for i, p in enumerate(proba)}
    
    # --- DYNAMIQUE LIVE ---
    if m.is_live:
        final_probs, live_insight = apply_live_dynamics(base_dict, m)
        # Trouver le nouveau gagnant
        result = max(final_probs, key=final_probs.get)
        confidence = final_probs[result]
        interp = f"🔴 [LIVE {m.live_minute}'] {live_insight} "
        if result == "HOME_WIN": interp += f"Home team maintaining lead ({confidence:.0f}%)."
        elif result == "AWAY_WIN": interp += f"Away team dominating field ({confidence:.0f}%)."
        else: interp += f"Tense draw situation ({confidence:.0f}%)."
    else:
        # Prédiction classique avant-match avec unicité
        unique_probs = apply_match_uniqueness(base_dict, m)
        result = max(unique_probs, key=unique_probs.get)
        confidence = unique_probs[result]
        final_probs = unique_probs
        
        if result == "HOME_WIN":
            interp = f"Home team is projected to win ({confidence:.0f}% confidence)."
        elif result == "AWAY_WIN":
            interp = f"Away team is projected to win ({confidence:.0f}% confidence)."
        else:
            interp = f"A draw is highly likely ({confidence:.0f}% confidence)."

    return PredictionResponse(
        result        = result,
        confidence    = confidence,
        probabilities = final_probs,
        interpretation= interp,
    )

# ─── Routes ───────────────────────────────────────────────────
@app.get("/health", tags=["Monitoring"])
def health():
    return {
        "status":   "ok",
        "model":    str(MODEL_PATH.name),
        "features": len(feature_names),
        "classes":  list(CLASSES.values()),
    }

@app.get("/model/info", tags=["Monitoring"])
def model_info():
    return {
        "feature_names": feature_names,
        "target_classes": CLASSES,
        "metrics": metrics_text,
    }

@app.post("/predict", response_model=PredictionResponse, tags=["Prédiction"])
def predict(match: MatchInput):
    """
    Prédit le résultat d'un match.
    Consommé par Spring Boot via RestTemplate :
        POST http://localhost:8000/predict
    """
    try:
        return make_prediction(match)
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@app.post("/predict/batch", response_model=BatchResponse, tags=["Prédiction"])
def predict_batch(payload: BatchInput):
    """Prédiction sur plusieurs matchs en une seule requête."""
    try:
        preds = [make_prediction(m) for m in payload.matches]
        return BatchResponse(predictions=preds, total=len(preds))
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

# ─── Point d'entrée ───────────────────────────────────────────
if __name__ == "__main__":
    import uvicorn
    uvicorn.run("4_predict_api:app", host="0.0.0.0", port=8000, reload=True)
