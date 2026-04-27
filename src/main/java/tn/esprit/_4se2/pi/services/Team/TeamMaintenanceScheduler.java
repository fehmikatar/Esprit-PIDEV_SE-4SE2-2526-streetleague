package tn.esprit._4se2.pi.services.Team;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TeamMaintenanceScheduler {

    private final TeamService teamService;

    @Scheduled(cron = "${app.team.scheduler.archive-cron:0 0 2 * * *}")
    public void archiveDormantTeams() {
        int archived = teamService.archiveDormantTeams(60);
        if (archived > 0) {
            log.info("Team scheduler archived {} dormant team(s).", archived);
        }
    }
}
