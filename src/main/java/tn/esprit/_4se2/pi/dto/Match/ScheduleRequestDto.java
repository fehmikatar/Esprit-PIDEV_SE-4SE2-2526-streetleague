package tn.esprit._4se2.pi.dto.Match;

import java.time.LocalDate;

public class ScheduleRequestDto {
    private LocalDate startDate;
    private LocalDate endDate;
    private int maxMatchesPerDay = 3;
    private int intervalMinutes = 90;

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public int getMaxMatchesPerDay() { return maxMatchesPerDay; }
    public void setMaxMatchesPerDay(int maxMatchesPerDay) { this.maxMatchesPerDay = maxMatchesPerDay; }
    public int getIntervalMinutes() { return intervalMinutes; }
    public void setIntervalMinutes(int intervalMinutes) { this.intervalMinutes = intervalMinutes; }
}
