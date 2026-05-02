package tn.esprit._4se2.pi.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit._4se2.pi.Enum.AppointmentStatus;
import tn.esprit._4se2.pi.entities.Appointment;
import tn.esprit._4se2.pi.entities.DietPlan;
import tn.esprit._4se2.pi.repositories.AppointmentRepository;
import tn.esprit._4se2.pi.repositories.DietPlanRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class HealthScheduler {

    private final AppointmentRepository appointmentRepository;
    private final DietPlanRepository dietPlanRepository;

    /**
     * S'exécute tous les jours à 01:00:00.
     * Met à jour le statut des rendez-vous passés et désactive les plans alimentaires expirés.
     */
    @Scheduled(cron = "0 * * * * ?")   // 1h du matin chaque jour
    @Transactional
    public void updateHealthData() {
        log.info("=== Début du scheduler santé ===");

        updatePastAppointments();

        deactivateExpiredDietPlans();

        log.info("=== Fin du scheduler santé ===");
    }

    private void updatePastAppointments() {
        LocalDateTime now = LocalDateTime.now();
        List<Appointment> pastAppointments = appointmentRepository
                .findByAppointmentDateBeforeAndStatusNotIn(now,
                        List.of(AppointmentStatus.COMPLETED, AppointmentStatus.CANCELLED));

        if (pastAppointments.isEmpty()) {
            log.info("Aucun rendez-vous passé à mettre à jour.");
            return;
        }

        for (Appointment apt : pastAppointments) {
            apt.setStatus(AppointmentStatus.COMPLETED);
            log.debug("Rendez-vous ID {} -> COMPLETED", apt.getId());
        }
        appointmentRepository.saveAll(pastAppointments);
        log.info("{} rendez-vous ont été marqués COMPLETED.", pastAppointments.size());
    }

    private void deactivateExpiredDietPlans() {
        LocalDate today = LocalDate.now();
        List<DietPlan> expiredPlans = dietPlanRepository.findByEndDateBeforeAndIsActiveTrue(today);

        if (expiredPlans.isEmpty()) {
            log.info("Aucun plan alimentaire expiré.");
            return;
        }

        for (DietPlan plan : expiredPlans) {
            plan.setIsActive(false);
            log.debug("Plan alimentaire ID {} désactivé (fin: {})", plan.getId(), plan.getEndDate());
        }
        dietPlanRepository.saveAll(expiredPlans);
        log.info("{} plans alimentaires désactivés.", expiredPlans.size());
    }
}