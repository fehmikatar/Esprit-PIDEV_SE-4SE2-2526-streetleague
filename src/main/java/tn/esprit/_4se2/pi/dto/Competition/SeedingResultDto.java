package tn.esprit._4se2.pi.dto.Competition;

public class SeedingResultDto {
    private Long teamId;
    private String teamName;
    private int seed;
    private double winRate;
    private double eloScore;
    private int wins;
    private int losses;
    private int draws;
    private int matchesPlayed;

    public SeedingResultDto() {}

    public SeedingResultDto(Long teamId, String teamName, int seed, double winRate, double eloScore,
                             int wins, int losses, int draws, int matchesPlayed) {
        this.teamId = teamId;
        this.teamName = teamName;
        this.seed = seed;
        this.winRate = winRate;
        this.eloScore = eloScore;
        this.wins = wins;
        this.losses = losses;
        this.draws = draws;
        this.matchesPlayed = matchesPlayed;
    }

    public Long getTeamId() { return teamId; }
    public void setTeamId(Long teamId) { this.teamId = teamId; }
    public String getTeamName() { return teamName; }
    public void setTeamName(String teamName) { this.teamName = teamName; }
    public int getSeed() { return seed; }
    public void setSeed(int seed) { this.seed = seed; }
    public double getWinRate() { return winRate; }
    public void setWinRate(double winRate) { this.winRate = winRate; }
    public double getEloScore() { return eloScore; }
    public void setEloScore(double eloScore) { this.eloScore = eloScore; }
    public int getWins() { return wins; }
    public void setWins(int wins) { this.wins = wins; }
    public int getLosses() { return losses; }
    public void setLosses(int losses) { this.losses = losses; }
    public int getDraws() { return draws; }
    public void setDraws(int draws) { this.draws = draws; }
    public int getMatchesPlayed() { return matchesPlayed; }
    public void setMatchesPlayed(int matchesPlayed) { this.matchesPlayed = matchesPlayed; }
}
