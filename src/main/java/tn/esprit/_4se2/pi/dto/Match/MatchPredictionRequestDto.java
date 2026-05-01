package tn.esprit._4se2.pi.dto.Match;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO envoyé à l'API FastAPI Python pour la prédiction du résultat d'un match.
 * POST http://localhost:8000/predict
 */
public class MatchPredictionRequestDto {

    @JsonProperty("home_rank")
    private int homeRank;

    @JsonProperty("away_rank")
    private int awayRank;

    @JsonProperty("home_goals_avg")
    private double homeGoalsAvg;

    @JsonProperty("away_goals_avg")
    private double awayGoalsAvg;

    @JsonProperty("home_conceded_avg")
    private double homeConcededAvg;

    @JsonProperty("away_conceded_avg")
    private double awayConcededAvg;

    @JsonProperty("home_wins_last5")
    private int homeWinsLast5;

    @JsonProperty("away_wins_last5")
    private int awayWinsLast5;

    @JsonProperty("home_rating_avg")
    private double homeRatingAvg;

    @JsonProperty("away_rating_avg")
    private double awayRatingAvg;

    @JsonProperty("is_neutral_venue")
    private int isNeutralVenue = 0;

    @JsonProperty("competition_format")
    private int competitionFormat = 0;

    // ── Variables Live (Dynamique de match) ──
    @JsonProperty("is_live")
    private boolean isLive = false;

    @JsonProperty("live_home_goals")
    private int liveHomeGoals = 0;

    @JsonProperty("live_away_goals")
    private int liveAwayGoals = 0;

    @JsonProperty("live_home_red_cards")
    private int liveHomeRedCards = 0;

    @JsonProperty("live_away_red_cards")
    private int liveAwayRedCards = 0;

    @JsonProperty("live_minute")
    private int liveMinute = 45;

    public MatchPredictionRequestDto() {}

    // ── Getters / Setters ────────────────────────────────────
    public int getHomeRank() { return homeRank; }
    public void setHomeRank(int homeRank) { this.homeRank = homeRank; }

    public int getAwayRank() { return awayRank; }
    public void setAwayRank(int awayRank) { this.awayRank = awayRank; }

    public double getHomeGoalsAvg() { return homeGoalsAvg; }
    public void setHomeGoalsAvg(double homeGoalsAvg) { this.homeGoalsAvg = homeGoalsAvg; }

    public double getAwayGoalsAvg() { return awayGoalsAvg; }
    public void setAwayGoalsAvg(double awayGoalsAvg) { this.awayGoalsAvg = awayGoalsAvg; }

    public double getHomeConcededAvg() { return homeConcededAvg; }
    public void setHomeConcededAvg(double homeConcededAvg) { this.homeConcededAvg = homeConcededAvg; }

    public double getAwayConcededAvg() { return awayConcededAvg; }
    public void setAwayConcededAvg(double awayConcededAvg) { this.awayConcededAvg = awayConcededAvg; }

    public int getHomeWinsLast5() { return homeWinsLast5; }
    public void setHomeWinsLast5(int homeWinsLast5) { this.homeWinsLast5 = homeWinsLast5; }

    public int getAwayWinsLast5() { return awayWinsLast5; }
    public void setAwayWinsLast5(int awayWinsLast5) { this.awayWinsLast5 = awayWinsLast5; }

    public double getHomeRatingAvg() { return homeRatingAvg; }
    public void setHomeRatingAvg(double homeRatingAvg) { this.homeRatingAvg = homeRatingAvg; }

    public double getAwayRatingAvg() { return awayRatingAvg; }
    public void setAwayRatingAvg(double awayRatingAvg) { this.awayRatingAvg = awayRatingAvg; }

    public int getIsNeutralVenue() { return isNeutralVenue; }
    public void setIsNeutralVenue(int isNeutralVenue) { this.isNeutralVenue = isNeutralVenue; }

    public int getCompetitionFormat() { return competitionFormat; }
    public void setCompetitionFormat(int competitionFormat) { this.competitionFormat = competitionFormat; }

    public boolean isLive() { return isLive; }
    public void setLive(boolean live) { isLive = live; }

    public int getLiveHomeGoals() { return liveHomeGoals; }
    public void setLiveHomeGoals(int liveHomeGoals) { this.liveHomeGoals = liveHomeGoals; }

    public int getLiveAwayGoals() { return liveAwayGoals; }
    public void setLiveAwayGoals(int liveAwayGoals) { this.liveAwayGoals = liveAwayGoals; }

    public int getLiveHomeRedCards() { return liveHomeRedCards; }
    public void setLiveHomeRedCards(int liveHomeRedCards) { this.liveHomeRedCards = liveHomeRedCards; }

    public int getLiveAwayRedCards() { return liveAwayRedCards; }
    public void setLiveAwayRedCards(int liveAwayRedCards) { this.liveAwayRedCards = liveAwayRedCards; }

    public int getLiveMinute() { return liveMinute; }
    public void setLiveMinute(int liveMinute) { this.liveMinute = liveMinute; }
}
