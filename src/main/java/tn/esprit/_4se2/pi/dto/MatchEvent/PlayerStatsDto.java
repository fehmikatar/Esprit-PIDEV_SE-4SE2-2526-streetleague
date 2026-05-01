package tn.esprit._4se2.pi.dto.MatchEvent;

import java.util.List;

public class PlayerStatsDto {
    // Top rankings
    private List<PlayerEntry> topScorers;
    private List<PlayerEntry> topAssists;
    private List<PlayerEntry> bestRated;
    private List<PlayerEntry> mostDisciplined; // 0 cards

    // All players
    private List<PlayerEntry> allPlayers;

    public static class PlayerEntry {
        private Long playerId;
        private String playerName;
        private Long teamId;
        private String teamName;

        // Stats
        private int goals;
        private int assists;
        private int yellowCards;
        private int redCards;
        private int fouls;
        private int substitutions;

        // Rating /5 and stars
        private double rating;
        private int stars;   // 1-5
        private boolean ejected; // has red card

        public PlayerEntry() {}

        public Long getPlayerId() { return playerId; }
        public void setPlayerId(Long playerId) { this.playerId = playerId; }
        public String getPlayerName() { return playerName; }
        public void setPlayerName(String playerName) { this.playerName = playerName; }
        public Long getTeamId() { return teamId; }
        public void setTeamId(Long teamId) { this.teamId = teamId; }
        public String getTeamName() { return teamName; }
        public void setTeamName(String teamName) { this.teamName = teamName; }
        public int getGoals() { return goals; }
        public void setGoals(int goals) { this.goals = goals; }
        public int getAssists() { return assists; }
        public void setAssists(int assists) { this.assists = assists; }
        public int getYellowCards() { return yellowCards; }
        public void setYellowCards(int yellowCards) { this.yellowCards = yellowCards; }
        public int getRedCards() { return redCards; }
        public void setRedCards(int redCards) { this.redCards = redCards; }
        public int getFouls() { return fouls; }
        public void setFouls(int fouls) { this.fouls = fouls; }
        public int getSubstitutions() { return substitutions; }
        public void setSubstitutions(int substitutions) { this.substitutions = substitutions; }
        public double getRating() { return rating; }
        public void setRating(double rating) { this.rating = rating; }
        public int getStars() { return stars; }
        public void setStars(int stars) { this.stars = stars; }
        public boolean isEjected() { return ejected; }
        public void setEjected(boolean ejected) { this.ejected = ejected; }
    }

    public List<PlayerEntry> getTopScorers() { return topScorers; }
    public void setTopScorers(List<PlayerEntry> topScorers) { this.topScorers = topScorers; }
    public List<PlayerEntry> getTopAssists() { return topAssists; }
    public void setTopAssists(List<PlayerEntry> topAssists) { this.topAssists = topAssists; }
    public List<PlayerEntry> getBestRated() { return bestRated; }
    public void setBestRated(List<PlayerEntry> bestRated) { this.bestRated = bestRated; }
    public List<PlayerEntry> getMostDisciplined() { return mostDisciplined; }
    public void setMostDisciplined(List<PlayerEntry> mostDisciplined) { this.mostDisciplined = mostDisciplined; }
    public List<PlayerEntry> getAllPlayers() { return allPlayers; }
    public void setAllPlayers(List<PlayerEntry> allPlayers) { this.allPlayers = allPlayers; }
}
