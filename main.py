"""
====================================================
  STREETLEAGUE — API FastAPI
  Fichier : main.py
  Lancer  : uvicorn main:app --reload --port 8000
====================================================
"""

from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel, Field, validator
from typing import List
import joblib
import numpy as np
import pandas as pd
import os

# ============================================================
# PARTIE A — Charger les modèles ML
# ============================================================
model    = joblib.load("models/best_model.pkl")
scaler   = joblib.load("models/scaler.pkl")
le_sport = joblib.load("models/le_sport.pkl")
le_badge = joblib.load("models/le_badge.pkl")
print("✅ Modèles StreetLeague chargés !")

# ============================================================
# PARTIE B — Application FastAPI
# ============================================================
app = FastAPI(
    title       = "🏆 StreetLeague ML API",
    description = "Prédit l'XP journalier et génère le plan de progression pour chaque athlète",
    version     = "1.0.0"
)

app.add_middleware(
    CORSMiddleware,
    allow_origins  = ["*"],
    allow_methods  = ["GET", "POST", "PUT", "DELETE", "OPTIONS"],
    allow_headers  = ["*"],
    allow_credentials = False,
)

# ============================================================
# PARTIE C — Schémas Pydantic (DTOs)
# ============================================================

class AthleteFeatures(BaseModel):
    """
    Données de l'athlète — tables MySQL StreetLeague :
      player_level : player_id, athlete_level, total_xp, sport_type
      performance  : match_score, teamwork_score, session_duration,
                     calories_burned, win_rate, streak_days, recovery_score
    """
    # ── table player_level ──────────────────────
    player_id      : int   = Field(..., example=42,          description="ID du joueur")
    athlete_level  : int   = Field(..., ge=1,  le=50,        example=18,    description="Niveau athlète (1-50)")
    total_xp       : float = Field(..., ge=0,                example=9500,  description="XP total actuel")
    sport_type     : str   = Field(...,                       example="football",
                                   description="Sport : football | basket | tennis | padel | running")

    # ── table performance ───────────────────────
    match_score     : float = Field(..., ge=0,  example=1200, description="Score moyen par match/session")
    teamwork_score  : float = Field(..., ge=0,  example=8,    description="Passes décisives / assists")
    session_duration: float = Field(..., ge=1,  example=75,   description="Durée session (minutes)")
    calories_burned : float = Field(..., ge=0,  example=550,  description="Calories brûlées par session")
    win_rate        : float = Field(..., ge=0, le=1, example=0.55, description="Taux de victoire (0-1)")
    streak_days     : int   = Field(..., ge=0,  example=12,   description="Jours consécutifs actifs")
    recovery_score  : float = Field(..., ge=0, le=10, example=7.5, description="Score récupération (0-10)")

    # ── Objectifs ───────────────────────────────
    xp_objective   : float = Field(..., gt=0,  example=18000, description="XP cible à atteindre")
    duration_days  : int   = Field(..., ge=1, le=365, example=30, description="Jours pour atteindre l'objectif")

    @validator("sport_type")
    def check_sport(cls, v):
        allowed = {"football", "basket", "tennis", "padel", "running"}
        if v not in allowed:
            raise ValueError(f"sport_type doit être : {allowed}")
        return v

    @validator("xp_objective")
    def check_xp(cls, v, values):
        if "total_xp" in values and v <= values["total_xp"]:
            raise ValueError("xp_objective doit être supérieur à total_xp")
        return v


class DayPlan(BaseModel):
    jour           : int
    xp_jour        : int
    xp_cumulatif   : int
    progression_pct: float


class Recommendations(BaseModel):
    sessions_par_jour       : int
    score_cible_par_session : int
    teamwork_cible          : int
    duree_session_min       : int
    xp_estime_par_session   : float
    niveau_defi             : str
    badge_actuel            : str
    conseils                : List[str]


class PredictionResult(BaseModel):
    player_id            : int
    sport_type           : str
    badge_actuel         : str
    xp_per_day_predicted : float
    difficulty_adapted   : int
    xp_gap               : float
    xp_needed_per_day    : float
    daily_plan           : List[DayPlan]
    recommendations      : Recommendations


# ============================================================
# PARTIE D — Fonctions métier
# ============================================================

def get_badge(level: int) -> str:
    if level <= 10:  return 'bronze'
    if level <= 25:  return 'silver'
    if level <= 40:  return 'gold'
    return 'elite'


def compute_features(a: AthleteFeatures):
    xp_gap        = a.xp_objective - a.total_xp
    score_per_min = a.match_score / max(a.session_duration, 1)
    xp_needed_pd  = xp_gap / max(a.duration_days, 1)
    level_xp_ratio= a.athlete_level / max(a.total_xp, 1) * 1000
    activity_idx  = a.match_score*0.4 + a.teamwork_score*20 + a.session_duration*2 + a.streak_days*10
    urgency       = xp_gap / max(a.duration_days, 1)
    session_eff   = a.match_score / max(a.session_duration, 1)
    health_sc     = a.recovery_score*50 + a.calories_burned*0.1 + a.win_rate*100
    badge         = get_badge(a.athlete_level)
    sport_enc     = int(le_sport.transform([a.sport_type])[0])
    badge_enc     = int(le_badge.transform([badge])[0])
    raw_diff      = xp_needed_pd / 200 + (a.athlete_level / 50) * 2
    difficulty    = int(np.clip(round(raw_diff), 1, 5))

    X = pd.DataFrame([{
        'athlete_level'     : a.athlete_level,    'total_xp'          : a.total_xp,
        'sport_type_enc'    : sport_enc,           'badge_type_enc'    : badge_enc,
        'match_score'       : a.match_score,       'teamwork_score'    : a.teamwork_score,
        'session_duration'  : a.session_duration,  'calories_burned'   : a.calories_burned,
        'win_rate'          : a.win_rate,           'streak_days'       : a.streak_days,
        'recovery_score'    : a.recovery_score,    'xp_objective'      : a.xp_objective,
        'duration_days'     : a.duration_days,     'xp_gap'            : xp_gap,
        'xp_needed_per_day' : xp_needed_pd,        'score_per_min'     : score_per_min,
        'activity_index'    : activity_idx,         'urgency'           : urgency,
        'level_xp_ratio'    : level_xp_ratio,      'session_efficiency': session_eff,
        'health_score'      : health_sc,            'difficulty'        : difficulty
    }])
    return X, difficulty, xp_gap, xp_needed_pd, badge


def generate_daily_plan(xp_per_day, duration_days, total_xp, xp_objective, sport_type) -> List[DayPlan]:
    days    = np.arange(1, duration_days + 1)
    sigmoid = 1 / (1 + np.exp(-0.15 * (days - duration_days * 0.4)))
    coefs   = {'football':(0.75,0.25),'basket':(0.70,0.30),
               'tennis':(0.80,0.20),'padel':(0.78,0.22),'running':(0.72,0.28)}
    a, b    = coefs.get(sport_type, (0.75, 0.25))
    weights = sigmoid * a + b
    xp_gap  = xp_objective - total_xp
    xp_d    = weights / weights.sum() * xp_gap
    xp_c    = total_xp + np.cumsum(xp_d)
    prog    = (xp_c - total_xp) / xp_gap * 100
    return [DayPlan(jour=int(d), xp_jour=int(round(xd)),
                    xp_cumulatif=int(round(xc)), progression_pct=round(float(p),1))
            for d, xd, xc, p in zip(days, xp_d, xp_c, prog)]


def generate_recommendations(xp_per_day, difficulty, sport_type, match_score,
                               teamwork_score, session_duration, streak_days, badge) -> Recommendations:
    sport_sess = {'football':2,'basket':2,'tennis':1,'padel':2,'running':1}
    sessions   = sport_sess.get(sport_type, 2) + (1 if difficulty >= 4 else 0)
    score_c    = int(max(match_score  * (1 + (difficulty-3)*0.10), 300))
    team_c     = int(max(teamwork_score * (1 + (difficulty-3)*0.08), 1))
    dur_c      = int(max(session_duration * (1 + (difficulty-3)*0.05), 20))
    labels     = {1:'🟢 Très facile',2:'🟡 Facile',3:'🟠 Modéré',4:'🔴 Difficile',5:'💀 Extrême'}
    conseils   = []
    if streak_days < 5:
        conseils.append("Active ton streak ! Joue chaque jour pour booster ton XP x1.5")
    if teamwork_score < 5:
        conseils.append("Améliore ton teamwork : passes et assists = +XP bonus")
    if session_duration < 45:
        conseils.append("Allonge tes sessions : 45min minimum pour maximiser les XP")
    if difficulty >= 4:
        conseils.append("Objectif ambitieux : participe aux tournois StreetLeague !")
    if not conseils:
        conseils.append("Parfait ! Continue sur cette lancée pour garder ton badge.")
    return Recommendations(
        sessions_par_jour=sessions, score_cible_par_session=score_c,
        teamwork_cible=team_c, duree_session_min=dur_c,
        xp_estime_par_session=round(xp_per_day/max(sessions,1),1),
        niveau_defi=labels[difficulty], badge_actuel=badge, conseils=conseils
    )


# ============================================================
# PARTIE E — Routes
# ============================================================

@app.get("/", tags=["Santé"])
def health():
    return {"status": "ok", "app": "StreetLeague ML API", "version": "1.0.0",
            "sports": ["football", "basket", "tennis", "padel", "running"]}


@app.post("/predict", response_model=PredictionResult, tags=["Prédiction"])
def predict(athlete: AthleteFeatures):
    """
    Prédit la progression optimale d'un athlète StreetLeague.
    - XP à gagner par jour
    - Plan journalier non-linéaire (courbe en S)
    - Badge actuel (bronze/silver/gold/elite)
    - Difficulté adaptée (1-5)
    - Recommandations par sport
    """
    try:
        X, difficulty, xp_gap, xp_needed_pd, badge = compute_features(athlete)
        xp_pred  = float(model.predict(X)[0])
        xp_pred  = max(30.0, round(xp_pred, 1))
        plan     = generate_daily_plan(xp_pred, athlete.duration_days,
                                       athlete.total_xp, athlete.xp_objective,
                                       athlete.sport_type)
        reco     = generate_recommendations(
                       xp_pred, difficulty, athlete.sport_type,
                       athlete.match_score, athlete.teamwork_score,
                       athlete.session_duration, athlete.streak_days, badge)
        return PredictionResult(
            player_id=athlete.player_id, sport_type=athlete.sport_type,
            badge_actuel=badge, xp_per_day_predicted=xp_pred,
            difficulty_adapted=difficulty, xp_gap=round(xp_gap,1),
            xp_needed_per_day=round(xp_needed_pd,1),
            daily_plan=plan, recommendations=reco
        )
    except ValueError as e:
        raise HTTPException(status_code=422, detail=str(e))
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))