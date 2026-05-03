"""
==============================================================
ÉTAPE 1 — DATASET  (tableau blanc: Dataset → Générée)
==============================================================
Génère ~1000 lignes de données de matchs StreetLeague.
Features extraites des entités Match + MatchEvent + PlayerStats.

Colonnes produites
------------------
  match_id, competition_id, competition_format,
  home_team_id, away_team_id,
  home_rank, away_rank,
  home_goals_avg, away_goals_avg,
  home_conceded_avg, away_conceded_avg,
  home_wins_last5, away_wins_last5,
  home_yellow_avg, away_yellow_avg,
  home_red_avg, away_red_avg,
  home_fouls_avg, away_fouls_avg,
  home_assists_avg, away_assists_avg,
  home_rating_avg, away_rating_avg,
  is_neutral_venue,
  home_score, away_score,
  result  ← TARGET: HOME_WIN | AWAY_WIN | DRAW
"""

import random
import csv
import os

random.seed(42)

# ── Constantes ──────────────────────────────────────────────
N_MATCHES        = 1000          # taille du dataset
N_TEAMS          = 30            # pool d'équipes
FORMATS          = [0, 1, 2]     # 0=SINGLE_ELIM  1=POULES  2=ROUND_ROBIN
OUTPUT_DIR       = os.path.join(os.path.dirname(__file__), "dataset")
OUTPUT_FILE      = os.path.join(OUTPUT_DIR, "match_dataset.csv")

os.makedirs(OUTPUT_DIR, exist_ok=True)

# ── Simulation des stats par équipe ─────────────────────────
team_profiles = {}
for t in range(1, N_TEAMS + 1):
    strength = random.uniform(0.3, 1.0)          # capacité générale
    team_profiles[t] = {
        "rank":         int((1 - strength) * 20) + 1,   # 1=meilleur
        "goals_avg":    round(random.uniform(0.5, 3.5) * strength, 2),
        "conceded_avg": round(random.uniform(0.5, 3.5) * (1 - strength + 0.3), 2),
        "wins_last5":   int(strength * 5 + random.uniform(-1, 1)),
        "yellow_avg":   round(random.uniform(0.5, 3.0), 2),
        "red_avg":      round(random.uniform(0.0, 0.5), 2),
        "fouls_avg":    round(random.uniform(5.0, 18.0), 2),
        "assists_avg":  round(random.uniform(0.3, 2.5) * strength, 2),
        "rating_avg":   round(random.uniform(2.5, 5.0) * strength + 1.0, 2),
    }
    # Clamp
    team_profiles[t]["wins_last5"]  = max(0, min(5, team_profiles[t]["wins_last5"]))
    team_profiles[t]["rating_avg"]  = min(5.0, team_profiles[t]["rating_avg"])

def simulate_result(home_id, away_id, is_neutral):
    """Simule un score final basé sur les profils d'équipes."""
    h  = team_profiles[home_id]
    a  = team_profiles[away_id]

    home_adv = 0.15 if not is_neutral else 0.0          # avantage terrain

    home_xg  = h["goals_avg"] - a["conceded_avg"] * 0.5 + home_adv
    away_xg  = a["goals_avg"] - h["conceded_avg"] * 0.5

    home_xg  = max(0.1, home_xg)
    away_xg  = max(0.1, away_xg)

    home_score = int(random.gauss(home_xg, 1.0))
    away_score = int(random.gauss(away_xg, 1.0))
    home_score = max(0, home_score)
    away_score = max(0, away_score)

    if home_score > away_score:
        result = "HOME_WIN"
    elif away_score > home_score:
        result = "AWAY_WIN"
    else:
        result = "DRAW"

    return home_score, away_score, result

# ── Génération des lignes ─────────────────────────────────────
FIELDNAMES = [
    "match_id", "competition_id", "competition_format",
    "home_team_id", "away_team_id",
    "home_rank", "away_rank",
    "home_goals_avg", "away_goals_avg",
    "home_conceded_avg", "away_conceded_avg",
    "home_wins_last5", "away_wins_last5",
    "home_yellow_avg", "away_yellow_avg",
    "home_red_avg", "away_red_avg",
    "home_fouls_avg", "away_fouls_avg",
    "home_assists_avg", "away_assists_avg",
    "home_rating_avg", "away_rating_avg",
    "is_neutral_venue",
    "home_score", "away_score",
    "result"
]

rows = []
for match_id in range(1, N_MATCHES + 1):
    home_id = random.randint(1, N_TEAMS)
    away_id = random.randint(1, N_TEAMS)
    while away_id == home_id:
        away_id = random.randint(1, N_TEAMS)

    competition_id     = random.randint(1, 10)
    competition_format = random.choice(FORMATS)
    is_neutral         = random.choice([0, 1])

    h = team_profiles[home_id]
    a = team_profiles[away_id]

    home_score, away_score, result = simulate_result(home_id, away_id, is_neutral)

    row = {
        "match_id":           match_id,
        "competition_id":     competition_id,
        "competition_format": competition_format,
        "home_team_id":       home_id,
        "away_team_id":       away_id,
        "home_rank":          h["rank"],
        "away_rank":          a["rank"],
        "home_goals_avg":     h["goals_avg"],
        "away_goals_avg":     a["goals_avg"],
        "home_conceded_avg":  h["conceded_avg"],
        "away_conceded_avg":  a["conceded_avg"],
        "home_wins_last5":    h["wins_last5"],
        "away_wins_last5":    a["wins_last5"],
        "home_yellow_avg":    h["yellow_avg"],
        "away_yellow_avg":    a["yellow_avg"],
        "home_red_avg":       h["red_avg"],
        "away_red_avg":       a["red_avg"],
        "home_fouls_avg":     h["fouls_avg"],
        "away_fouls_avg":     a["fouls_avg"],
        "home_assists_avg":   h["assists_avg"],
        "away_assists_avg":   a["assists_avg"],
        "home_rating_avg":    h["rating_avg"],
        "away_rating_avg":    a["rating_avg"],
        "is_neutral_venue":   is_neutral,
        "home_score":         home_score,
        "away_score":         away_score,
        "result":             result,
    }
    rows.append(row)

with open(OUTPUT_FILE, "w", newline="", encoding="utf-8") as f:
    writer = csv.DictWriter(f, fieldnames=FIELDNAMES)
    writer.writeheader()
    writer.writerows(rows)

# ── Résumé ────────────────────────────────────────────────────
from collections import Counter
counts = Counter(r["result"] for r in rows)
print(f"✅ Dataset généré : {OUTPUT_FILE}")
print(f"   Lignes   : {len(rows)}")
print(f"   Colonnes : {len(FIELDNAMES)}")
print(f"   Distribution des résultats :")
for k, v in counts.items():
    print(f"      {k:<12} → {v:>4} matchs ({v/len(rows)*100:.1f}%)")
