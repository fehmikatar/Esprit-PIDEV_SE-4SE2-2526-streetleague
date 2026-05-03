package tn.esprit._4se2.pi.dto.Match;

public class MatchRatingDto {

    private TeamRating homeRating;
    private TeamRating awayRating;
    private Long mvpTeamId;
    private String mvpTeamName;

    public static class TeamRating {
        private Long teamId;
        private String teamName;
        private double score;      // /10
        private String grade;      // A+, A, B, C, D
        private RatingBreakdown breakdown;

        public TeamRating() {}

        public Long getTeamId() { return teamId; }
        public void setTeamId(Long teamId) { this.teamId = teamId; }
        public String getTeamName() { return teamName; }
        public void setTeamName(String teamName) { this.teamName = teamName; }
        public double getScore() { return score; }
        public void setScore(double score) { this.score = score; }
        public String getGrade() { return grade; }
        public void setGrade(String grade) { this.grade = grade; }
        public RatingBreakdown getBreakdown() { return breakdown; }
        public void setBreakdown(RatingBreakdown breakdown) { this.breakdown = breakdown; }
    }

    public static class RatingBreakdown {
        private double offensive;    // 35%
        private double defensive;    // 25%
        private double shooting;     // 20%
        private double discipline;   // 10%
        private double momentum;     // 10%

        public RatingBreakdown() {}
        public RatingBreakdown(double offensive, double defensive, double shooting,
                               double discipline, double momentum) {
            this.offensive = offensive;
            this.defensive = defensive;
            this.shooting = shooting;
            this.discipline = discipline;
            this.momentum = momentum;
        }

        public double getOffensive() { return offensive; }
        public void setOffensive(double offensive) { this.offensive = offensive; }
        public double getDefensive() { return defensive; }
        public void setDefensive(double defensive) { this.defensive = defensive; }
        public double getShooting() { return shooting; }
        public void setShooting(double shooting) { this.shooting = shooting; }
        public double getDiscipline() { return discipline; }
        public void setDiscipline(double discipline) { this.discipline = discipline; }
        public double getMomentum() { return momentum; }
        public void setMomentum(double momentum) { this.momentum = momentum; }
    }

    // Getters/Setters
    public TeamRating getHomeRating() { return homeRating; }
    public void setHomeRating(TeamRating homeRating) { this.homeRating = homeRating; }
    public TeamRating getAwayRating() { return awayRating; }
    public void setAwayRating(TeamRating awayRating) { this.awayRating = awayRating; }
    public Long getMvpTeamId() { return mvpTeamId; }
    public void setMvpTeamId(Long mvpTeamId) { this.mvpTeamId = mvpTeamId; }
    public String getMvpTeamName() { return mvpTeamName; }
    public void setMvpTeamName(String mvpTeamName) { this.mvpTeamName = mvpTeamName; }
}
