package tn.esprit._4se2.pi.dto.Competition;

import java.util.List;

public class SeedingRequestDto {
    private List<ManualSeedEntry> seeds;

    public List<ManualSeedEntry> getSeeds() { return seeds; }
    public void setSeeds(List<ManualSeedEntry> seeds) { this.seeds = seeds; }

    public static class ManualSeedEntry {
        private Long teamId;
        private int seed;

        public Long getTeamId() { return teamId; }
        public void setTeamId(Long teamId) { this.teamId = teamId; }
        public int getSeed() { return seed; }
        public void setSeed(int seed) { this.seed = seed; }
    }
}
