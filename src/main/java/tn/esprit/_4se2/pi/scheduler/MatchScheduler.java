package tn.esprit._4se2.pi.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit._4se2.pi.Enum.CompetitionStatus;
import tn.esprit._4se2.pi.Enum.MatchStatus;
import tn.esprit._4se2.pi.entities.Competition;
import tn.esprit._4se2.pi.entities.Match;
import tn.esprit._4se2.pi.repositories.CompetitionRepository;
import tn.esprit._4se2.pi.repositories.MatchRepository;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class MatchScheduler {

    private final MatchRepository matchRepository;
    private final CompetitionRepository competitionRepository;


    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void startScheduledMatches() {
        log.info("Vérification des matchs à démarrer...");
        List<Match> matchesToStart = matchRepository.findByStatusAndScheduledAtBefore(MatchStatus.SCHEDULED, LocalDateTime.now());
        
        if (!matchesToStart.isEmpty()) {
            matchesToStart.forEach(m -> m.setStatus(MatchStatus.LIVE));
            matchRepository.saveAll(matchesToStart);
            log.info("{} matchs sont passés en LIVE.", matchesToStart.size());
        }
    }


    @Scheduled(cron = "0 */5 * * * *")
    @Transactional
    public void finishLiveMatches() {
        log.info("Vérification des matchs à terminer...");
        LocalDateTime threshold = LocalDateTime.now().minusHours(2);
        List<Match> matchesToFinish = matchRepository.findByStatusAndScheduledAtBefore(MatchStatus.LIVE, threshold);
        
        if (!matchesToFinish.isEmpty()) {
            matchesToFinish.forEach(m -> m.setStatus(MatchStatus.FINISHED));
            matchRepository.saveAll(matchesToFinish);
            log.info("{} matchs sont passés en FINISHED.", matchesToFinish.size());
            
            updateCompetitionsStatus();
        }
    }

    private void updateCompetitionsStatus() {
        List<Competition> ongoingCompetitions = competitionRepository.findByStatus(CompetitionStatus.ONGOING);
        
        for (Competition competition : ongoingCompetitions) {
            List<Match> matches = matchRepository.findByCompetitionId(competition.getId());
            boolean allFinished = !matches.isEmpty() && matches.stream().allMatch(m -> m.getStatus() == MatchStatus.FINISHED);
            
            if (allFinished) {
                competition.setStatus(CompetitionStatus.FINISHED);
                competitionRepository.save(competition);
                log.info("La compétition '{}' est maintenant terminée.", competition.getName());
            }
        }
    }
}
