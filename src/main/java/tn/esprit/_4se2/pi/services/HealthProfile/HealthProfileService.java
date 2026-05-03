package tn.esprit._4se2.pi.services.HealthProfile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit._4se2.pi.dto.HealthProfile.ActivityRecommendationDto;
import tn.esprit._4se2.pi.dto.HealthProfile.HealthProfileRequest;
import tn.esprit._4se2.pi.dto.HealthProfile.HealthProfileResponse;
import tn.esprit._4se2.pi.entities.HealthProfile;
import tn.esprit._4se2.pi.entities.MedicalRecord;
import tn.esprit._4se2.pi.entities.User;
import tn.esprit._4se2.pi.Enum.RecoveryStatus;
import tn.esprit._4se2.pi.Enum.InjuryType;
import tn.esprit._4se2.pi.mappers.HealthProfileMapper;
import tn.esprit._4se2.pi.repositories.HealthProfileRepository;
import tn.esprit._4se2.pi.repositories.MedicalRecordRepository;
import tn.esprit._4se2.pi.repositories.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import tn.esprit._4se2.pi.dto.Notification.NotificationRequest;
import tn.esprit._4se2.pi.services.Notification.NotificationService;
import tn.esprit._4se2.pi.entities.HealthMetrics;
import tn.esprit._4se2.pi.dto.Athlet.ChatResponse;
import tn.esprit._4se2.pi.dto.Athlet.AthleteRequest;
import tn.esprit._4se2.pi.repositories.HealthMetricsRepository;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class HealthProfileService implements IHealthProfileService {

    private final HealthProfileRepository healthProfileRepository;
    private final MedicalRecordRepository medicalRecordRepository;
    private final UserRepository userRepository;
    private final HealthProfileMapper healthProfileMapper;
    private final NotificationService notificationService;
    private final HealthMetricsRepository healthMetricsRepository;

    @Override
    public double predictScore(AthleteRequest req) {
        double[] coeffs = {
                -1.6990426753051222e-14, -2.000000000000375, 9.325873406851315e-15, 
                9.999999999999957, 5.017752706393264e-13, 3.117899818536052e-14, 
                -1.0534035503693279e-11, 3.205766255481423e-11, 1.0250914700415947e-14, 
                10.000000000000004, 4.133293673555543e-13, -1.7850233481269383e-13
        };
        double intercept = 6.425880753005296e-09;
        double[] x = { req.getDuration(), req.getIntensity(), req.getTrainingLoad(), req.getSleepHours(), req.getSleepQuality(), req.getFatigueLevel(), req.getHydrationLiters(), req.getBmi(), req.getWeightKg(), req.getCaloriesIn(), req.getNutritionAdherence(), (double)req.getSorenessCode() };
        double score = intercept;
        for (int i = 0; i < coeffs.length; i++) score += coeffs[i] * x[i];
        return score;
    }

    @Override
    public String generateAssessment(AthleteRequest req, double score) {
        StringBuilder sb = new StringBuilder();
        String status = score > 30000 ? "Stable" : score > 20000 ? "Vigilant" : "Alert";
        String color = status.equals("Stable") ? "#1DB954" : status.equals("Vigilant") ? "#F97316" : "#EF4444";
        sb.append("<div style='font-family: sans-serif; line-height: 1.6;'>")
          .append("<h3 style='color:").append(color).append(";'>Classification : ").append(status).append("</h3>")
          .append("<h4>Analyse des Risques :</h4><ul>");
        if (req.getBmi() > 27) sb.append("<li><b>Risque Métabolique :</b> IMC élevé (").append(String.format("%.1f", req.getBmi())).append(").</li>");
        if (req.getSleepHours() < 6) sb.append("<li><b>Déficit Neurologique :</b> Sommeil critique.</li>");
        if (req.getFatigueLevel() > 4) sb.append("<li><b>Surmenage :</b> Risque de blessure élevé.</li>");
        if (req.getHydrationLiters() < 1.5) sb.append("<li><b>Déséquilibre :</b> Hydratation insuffisante.</li>");
        if (sb.toString().endsWith("<ul>")) sb.append("<li>Aucun risque majeur immédiat détecté.</li>");
        sb.append("</ul></div>");
        return sb.toString();
    }

    @Override
    public String predictFuture(AthleteRequest req, double score) {
        double trend = (req.getSleepHours() * 2) + (req.getHydrationLiters() * 5) - (req.getFatigueLevel() * 10);
        if (trend > 10) return "Projection à 30 jours : Amélioration continue (+15%).";
        if (trend < -5) return "Projection à 30 jours : Risque de dégradation (-20%).";
        return "Projection à 30 jours : État stationnaire.";
    }

    @Override
    public void saveScore(Long userId, Double score, String assessment, String prediction) {
        HealthProfile profile = healthProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));
        profile.setHealthScore(score);
        profile.setHealthAssessment(assessment);
        profile.setHealthFuturePrediction(prediction);
        healthProfileRepository.save(profile);

        HealthMetrics metrics = new HealthMetrics();
        metrics.setHealthProfile(profile);
        metrics.setHealthScore(score);
        metrics.setAssessment(assessment);
        metrics.setPrediction(prediction);
        metrics.setMeasuredAt(LocalDateTime.now());
        healthMetricsRepository.save(metrics);
    }

    @Override
    public List<HealthMetrics> getHistory(Long userId) {
        HealthProfile profile = healthProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));
        return healthMetricsRepository.findByHealthProfileIdOrderByMeasuredAtAsc(profile.getId());
    }

    @Override
    public ChatResponse traiteChatMessage(String message) {
        String msg = message.toLowerCase().trim();
        if (msg.startsWith("save")) {
            return new ChatResponse("Saving your health profile... Check the progress chart above once done.");
        }

        if (msg.contains("calcule") || msg.contains("prédiction") || msg.contains("health score") || msg.contains("score") || msg.contains("bilan")) {
            AthleteRequest req = extraireValeurs(message);
            double score = predictScore(req);
            String assessment = generateAssessment(req, score);
            String prediction = predictFuture(req, score);
            
            return new ChatResponse();
        }
        
        if (msg.contains("sommeil")) return new ChatResponse("Dormez 7 à 9 heures pour réduire les blessures.");
        if (msg.contains("eau")) return new ChatResponse("Buvez 2.5 à 3 litres d'eau par jour.");
        
        return new ChatResponse("Demandez un calcul ou posez une question sur le sommeil/hydratation.");
    }

    private AthleteRequest extraireValeurs(String texte) {
        AthleteRequest req = new AthleteRequest();
        req.setDuration(extraireDouble(texte, "(\\d+)\\s*(minutes|min)", 45.0));
        req.setIntensity((int)extraireDouble(texte, "intensité\\s*(\\d+)", 5.0));
        req.setSleepHours(extraireDouble(texte, "sommeil\\s*(\\d+)", 7.0));
        req.setCaloriesIn(extraireDouble(texte, "calories\\s*(\\d+)", 2500.0));
        req.setTrainingLoad(req.getDuration() * req.getIntensity());
        req.setSleepQuality(4); req.setFatigueLevel(2); req.setHydrationLiters(2.2); req.setBmi(22.5); req.setWeightKg(75.0); req.setNutritionAdherence(3); req.setSorenessCode(2);
        return req;
    }

    private double extraireDouble(String texte, String regex, double def) {
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(regex, java.util.regex.Pattern.CASE_INSENSITIVE);
        java.util.regex.Matcher m = p.matcher(texte);
        return m.find() ? Double.parseDouble(m.group(1)) : def;
    }

    @Override
    public HealthProfileResponse createHealthProfile(HealthProfileRequest request) {
        log.info("Creating health profile for user id: {}", request.getUserId());

        // Check if user exists
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + request.getUserId()));

        // Check if user already has a health profile
        if (healthProfileRepository.findByUserId(request.getUserId()).isPresent()) {
            throw new RuntimeException("User already has a health profile");
        }

        HealthProfile profile = healthProfileMapper.toEntity(request);
        profile.setUser(user);

        HealthProfile saved = healthProfileRepository.save(profile);
        log.info("Health profile created with id: {}", saved.getId());

        return healthProfileMapper.toResponse(saved);
    }
    @Override
    public List<HealthProfileResponse> getAllHealthProfiles() {
        return healthProfileRepository.findAll()
                .stream()
                .map(healthProfileMapper::toResponse)   // ✅ utilisation du mapper
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public HealthProfileResponse getHealthProfileById(Long id) {
        log.info("Fetching health profile with id: {}", id);
        return healthProfileRepository.findById(id)
                .map(healthProfileMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Health profile not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public HealthProfileResponse getHealthProfileByUserId(Long userId) {
        log.info("Fetching health profile for user id: {}", userId);
        return healthProfileRepository.findByUserId(userId)
                .map(healthProfileMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Health profile not found for user id: " + userId));
    }

    @Override
    public HealthProfileResponse updateHealthProfile(Long id, HealthProfileRequest request) {
        log.info("Updating health profile with id: {}", id);

        HealthProfile existing = healthProfileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Health profile not found with id: " + id));

        // If user changed, verify and update
        if (!existing.getUser().getId().equals(request.getUserId())) {
            User newUser = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + request.getUserId()));
            // Check that new user doesn't already have a profile (optional)
            if (healthProfileRepository.findByUserId(request.getUserId()).isPresent() &&
                    !healthProfileRepository.findByUserId(request.getUserId()).get().getId().equals(id)) {
                throw new RuntimeException("Another health profile already exists for user " + request.getUserId());
            }
            existing.setUser(newUser);
        }

        healthProfileMapper.updateEntity(request, existing);
        HealthProfile updated = healthProfileRepository.save(existing);
        log.info("Health profile updated with id: {}", id);

        return healthProfileMapper.toResponse(updated);
    }

    @Override
    public void deleteHealthProfile(Long id) {
        log.info("Deleting health profile with id: {}", id);
        if (!healthProfileRepository.existsById(id)) {
            throw new RuntimeException("Health profile not found with id: " + id);
        }
        healthProfileRepository.deleteById(id);
        log.info("Health profile deleted with id: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActivityRecommendationDto> generateActivityPlanByUserId(Long userId) {
        int currentWeek = LocalDate.now().get(WeekFields.of(Locale.FRANCE).weekOfWeekBasedYear());
        int weekCycle = ((currentWeek - 1) % 4) + 1;
        return generateActivityPlanByUserIdAndWeek(userId, weekCycle);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActivityRecommendationDto> generateActivityPlanByUserIdAndWeek(Long userId, int weekNumber) {
        log.info("Generating activity plan for user {}, week {}", userId, weekNumber);
        HealthProfile profile = healthProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Health profile not found for user id: " + userId));

        Double bmi = profile.getBmi();
        if (bmi == null) {
            throw new RuntimeException("Cannot generate activity plan: BMI is not calculated.");
        }

        // Fetch active medical records
        List<MedicalRecord> records = medicalRecordRepository.findByHealthProfileId(profile.getId());
        boolean hasActiveInjury = records.stream().anyMatch(r ->
                r.getRecoveryStatus() == RecoveryStatus.IN_PROGRESS ||
                r.getRecoveryStatus() == RecoveryStatus.PENDING ||
                r.getRecoveryStatus() == RecoveryStatus.COMPLICATED);

        boolean hasLowerBodyInjury = records.stream().anyMatch(r ->
                (r.getRecoveryStatus() == RecoveryStatus.IN_PROGRESS || r.getRecoveryStatus() == RecoveryStatus.PENDING) &&
                (r.getInjuryType() == InjuryType.SPRAIN || r.getInjuryType() == InjuryType.FRACTURE ||
                 r.getInjuryType() == InjuryType.STRESS_FRACTURE || r.getInjuryType() == InjuryType.LIGAMENT_TEAR));

        boolean hasUpperBodyInjury = records.stream().anyMatch(r ->
                (r.getRecoveryStatus() == RecoveryStatus.IN_PROGRESS || r.getRecoveryStatus() == RecoveryStatus.PENDING) &&
                (r.getInjuryType() == InjuryType.MUSCLE_STRAIN || r.getInjuryType() == InjuryType.MUSCLE_TEAR ||
                 r.getInjuryType() == InjuryType.DISLOCATION));

        boolean hasConcussion = records.stream().anyMatch(r ->
                r.getRecoveryStatus() != RecoveryStatus.COMPLETED && r.getInjuryType() == InjuryType.CONCUSSION);

        List<ActivityRecommendationDto> plan = new ArrayList<>();

        // ===== BLESSURE ACTIVE → programme adapté récupération =====
        if (hasConcussion) {
            plan.add(new ActivityRecommendationDto("Lundi", "Repos complet", 0, "Faible", "⚠️ Commotion cérébrale détectée. Repos absolu recommandé."));
            plan.add(new ActivityRecommendationDto("Mardi", "Repos complet", 0, "Faible", "Pas d'activité physique ni d'écran prolongé."));
            plan.add(new ActivityRecommendationDto("Mercredi", "Marche très lente (si autorisé)", 10, "Faible", "Uniquement si le médecin l'autorise."));
            plan.add(new ActivityRecommendationDto("Jeudi", "Repos complet", 0, "Faible", "Continuer le repos."));
            plan.add(new ActivityRecommendationDto("Vendredi", "Repos complet", 0, "Faible", "Consultation médicale recommandée."));
            plan.add(new ActivityRecommendationDto("Samedi", "Repos complet", 0, "Faible", "Repos."));
            plan.add(new ActivityRecommendationDto("Dimanche", "Repos complet", 0, "Faible", "Repos."));
            return plan;
        }

        if (hasActiveInjury && hasLowerBodyInjury) {
            String note = "🩹 Blessure bas du corps — focus haut du corps. ";
            int durMod = (bmi < 25) ? 0 : (bmi < 30) ? -5 : -10; // Durée réduite si surpoids pour ménager les articulations
            
            plan.add(new ActivityRecommendationDto("Lundi", "Musculation haut du corps", 30 + durMod, "Faible", note));
            plan.add(new ActivityRecommendationDto("Mardi", "Repos", 0, "Faible", "Récupération."));
            plan.add(new ActivityRecommendationDto("Mercredi", "Natation bras uniquement", 25 + durMod, "Faible", "Nage avec pull-buoy."));
            plan.add(new ActivityRecommendationDto("Jeudi", "Étirements haut du corps", 20, "Faible", "Mobilité épaules et dos."));
            plan.add(new ActivityRecommendationDto("Vendredi", "Musculation bras & épaules", 30 + durMod, "Faible à Modérée", "Exercices assis sans charge sur les jambes."));
            plan.add(new ActivityRecommendationDto("Samedi", "Repos", 0, "Faible", "Repos."));
            plan.add(new ActivityRecommendationDto("Dimanche", "Repos", 0, "Faible", "Repos complet."));
            return plan;
        }

        if (hasActiveInjury && hasUpperBodyInjury) {
            String note = "🩹 Blessure haut du corps — focus bas du corps. ";
            int durMod = (bmi < 25) ? 5 : (bmi < 30) ? 0 : -5;
            
            plan.add(new ActivityRecommendationDto("Lundi", "Marche modérée", 30 + durMod, "Faible", note));
            plan.add(new ActivityRecommendationDto("Mardi", "Vélo d'appartement", 25 + durMod, "Faible à Modérée", "Cardio sans solliciter le haut du corps."));
            plan.add(new ActivityRecommendationDto("Mercredi", "Repos", 0, "Faible", "Récupération."));
            plan.add(new ActivityRecommendationDto("Jeudi", "Squats & fentes légères", 25 + durMod, "Faible", "Renforcement bas du corps sans impact."));
            plan.add(new ActivityRecommendationDto("Vendredi", "Marche active", 35 + durMod, "Faible", "Cardio doux."));
            plan.add(new ActivityRecommendationDto("Samedi", "Étirements bas du corps", 20, "Faible", "Souplesse."));
            plan.add(new ActivityRecommendationDto("Dimanche", "Repos", 0, "Faible", "Repos."));
            return plan;
        }

        if (hasActiveInjury) {
            plan.add(new ActivityRecommendationDto("Lundi", "Marche douce", 20, "Faible", "🩹 Blessure détectée — programme allégé."));
            plan.add(new ActivityRecommendationDto("Mardi", "Repos", 0, "Faible", "Récupération."));
            plan.add(new ActivityRecommendationDto("Mercredi", "Yoga doux / Étirements", 20, "Faible", "Mobilité sans douleur."));
            plan.add(new ActivityRecommendationDto("Jeudi", "Repos", 0, "Faible", "Récupération."));
            plan.add(new ActivityRecommendationDto("Vendredi", "Aquagym légère", 20, "Faible", "Exercice doux en milieu aquatique."));
            plan.add(new ActivityRecommendationDto("Samedi", "Repos", 0, "Faible", "Repos."));
            plan.add(new ActivityRecommendationDto("Dimanche", "Repos", 0, "Faible", "Repos complet."));
            return plan;
        }

        // ===== PAS DE BLESSURE → programme normal selon IMC + semaine + genre =====
        int week = ((weekNumber - 1) % 4) + 1;
        tn.esprit._4se2.pi.Enum.Gender gender = profile.getGender() != null ? profile.getGender() : tn.esprit._4se2.pi.Enum.Gender.MALE;
        
        String[][] activities = getActivitiesForBmiAndWeek(bmi, week, gender);
        String[] jours = {"Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi", "Dimanche"};
        for (int i = 0; i < 7; i++) {
            plan.add(new ActivityRecommendationDto(jours[i], activities[i][0],
                    Integer.parseInt(activities[i][1]), activities[i][2], activities[i][3]));
        }
        return plan;
    }

    private String[][] getActivitiesForBmiAndWeek(double bmi, int week, tn.esprit._4se2.pi.Enum.Gender gender) {
        boolean isMale = gender == tn.esprit._4se2.pi.Enum.Gender.MALE;

        if (bmi < 18.5) {
            // SOUS-POIDS
            if (isMale) {
                return switch (week) {
                    case 1 -> new String[][]{
                            {"Musculation (Haut)", "50", "Modérée", "Haltères, pompes, tractions lourdes."},
                            {"Yoga & Souplesse", "30", "Faible", "Récupération active."},
                            {"Musculation (Bas)", "50", "Modérée", "Squats, fentes avec charges."},
                            {"Repos", "0", "Faible", "Croissance musculaire."},
                            {"Full Body Puissance", "45", "Élevée", "Poids libres, polyarticulaire."},
                            {"Marche active", "30", "Faible", "Entretien cardio."},
                            {"Repos", "0", "Faible", "Repos."}
                    };
                    default -> new String[][]{
                            {"Push Day", "55", "Élevée", "Pecs, épaules, triceps."},
                            {"Marche & Gainage", "30", "Faible", "Stabilité core."},
                            {"Pull Day", "55", "Élevée", "Dos, biceps."},
                            {"Repos", "0", "Faible", "Récupération."},
                            {"Leg Day", "50", "Élevée", "Squat, deadlift."},
                            {"Natation", "35", "Modérée", "Volume musculaire."},
                            {"Repos", "0", "Faible", "Repos."}
                    };
                };
            } else {
                return switch (week) {
                    case 1 -> new String[][]{
                            {"Pilates", "45", "Modérée", "Renforcement profond."},
                            {"Marche tonique", "40", "Faible", "Cardio doux."},
                            {"Yoga Vinyasa", "50", "Modérée", "Souplesse et tonus."},
                            {"Repos", "0", "Faible", "Récupération."},
                            {"Renforcement Full Body", "40", "Modérée", "Petits haltères, élastiques."},
                            {"Danse / Zumba", "45", "Modérée", "Coordination."},
                            {"Repos", "0", "Faible", "Repos."}
                    };
                    default -> new String[][]{
                            {"Barre au sol", "50", "Modérée", "Posture et tonification."},
                            {"Marche rapide", "45", "Faible", "Endurance."},
                            {"Yoga Power", "45", "Modérée", "Force et équilibre."},
                            {"Repos", "0", "Faible", "Récupération."},
                            {"Circuit Training", "40", "Modérée", "HIIT léger."},
                            {"Natation douce", "40", "Faible", "Mobilité."},
                            {"Repos", "0", "Faible", "Repos."}
                    };
                };
            }
        } else if (bmi < 25) {
            // NORMAL
            if (isMale) {
                return switch (week) {
                    case 1 -> new String[][]{
                            {"Running (6-8km)", "45", "Modérée", "Rythme régulier."},
                            {"Cross-training", "50", "Élevée", "Burpees, kettlebell, tractions."},
                            {"Natation (Crawl)", "45", "Élevée", "Sprints aquatiques."},
                            {"Repos actif", "30", "Faible", "Étirements."},
                            {"HIIT Explosif", "35", "Élevée", "15s sprint / 15s repos."},
                            {"Football / Basket", "60", "Élevée", "Cardio intense."},
                            {"Repos", "0", "Faible", "Repos."}
                    };
                    default -> new String[][]{
                            {"Trail Running", "60", "Élevée", "Terrain varié."},
                            {"Musculation Force", "55", "Élevée", "Charges lourdes, reps basses."},
                            {"Cyclisme", "75", "Modérée", "Sortie route."},
                            {"Repos", "0", "Faible", "Récupération."},
                            {"Boxe / Crossfit", "50", "Élevée", "Intensité maximale."},
                            {"Marche longue", "90", "Faible", "Récupération active."},
                            {"Repos", "0", "Faible", "Repos."}
                    };
                };
            } else {
                return switch (week) {
                    case 1 -> new String[][]{
                            {"Jogging (5km)", "40", "Modérée", "Endurance fondamentale."},
                            {"Fitness / HIIT", "45", "Modérée", "Sculpt & Tone."},
                            {"Natation (Brasse/Dos)", "45", "Modérée", "Drainage lymphatique."},
                            {"Yoga / Stretching", "40", "Faible", "Mobilité."},
                            {"Tabata Cardio", "30", "Élevée", "Brûle-graisse."},
                            {"Tennis / Badminton", "60", "Modérée", "Social & Cardio."},
                            {"Repos", "0", "Faible", "Repos."}
                    };
                    default -> new String[][]{
                            {"Pilates Avancé", "50", "Modérée", "Sangle abdominale."},
                            {"Vélo Elliptique", "45", "Modérée", "Cardio sans impact."},
                            {"Running fractionné", "35", "Élevée", "2min rapide / 1min lent."},
                            {"Repos", "0", "Faible", "Récupération."},
                            {"Yoga dynamique", "55", "Modérée", "Vinyasa Flow."},
                            {"Randonnée", "120", "Modérée", "Sortie weekend."},
                            {"Repos", "0", "Faible", "Repos."}
                    };
                };
            }
        } else if (bmi < 30) {
            // SURPOIDS
            if (isMale) {
                return switch (week) {
                    case 1 -> new String[][]{
                            {"Marche rapide (Dénivelé)", "50", "Modérée", "Inclinaison tapis ou colline."},
                            {"Circuit Renforcement", "45", "Modérée", "Poids de corps, focus jambes."},
                            {"Aquagym tonique", "45", "Modérée", "Résistance de l'eau."},
                            {"Repos", "0", "Faible", "Récupération."},
                            {"Vélo stationnaire", "45", "Modérée", "Résistance moyenne."},
                            {"Marche endurance", "70", "Faible", "Volume calorique."},
                            {"Yoga", "30", "Faible", "Détente."}
                    };
                    default -> new String[][]{
                            {"Rameur", "30", "Modérée", "Cardio complet sans impact."},
                            {"Marche Nordique", "50", "Modérée", "Engagement bras/jambes."},
                            {"Natation (Palmes)", "40", "Modérée", "Tonus jambes."},
                            {"Repos", "0", "Faible", "Récupération."},
                            {"Elliptique", "45", "Modérée", "Brûlage calories."},
                            {"Balade vélo", "60", "Faible", "Sortie plein air."},
                            {"Repos", "0", "Faible", "Repos."}
                    };
                };
            } else {
                return switch (week) {
                    case 1 -> new String[][]{
                            {"Aquagym", "45", "Modérée", "Doux pour les articulations."},
                            {"Marche active", "45", "Modérée", "Respiration contrôlée."},
                            {"Yoga pour débutants", "40", "Faible", "Équilibre."},
                            {"Repos", "0", "Faible", "Récupération."},
                            {"Pilates léger", "40", "Modérée", "Posture."},
                            {"Marche en forêt", "60", "Faible", "Bien-être."},
                            {"Repos", "0", "Faible", "Repos."}
                    };
                    default -> new String[][]{
                            {"Vélo aquatique", "35", "Modérée", "Anti-cellulite & Cardio."},
                            {"Marche rapide (Plat)", "50", "Modérée", "Rythme constant."},
                            {"Stretching", "45", "Faible", "Détente musculaire."},
                            {"Repos", "0", "Faible", "Récupération."},
                            {"Elliptique doux", "40", "Faible", "Mouvement fluide."},
                            {"Marche avec pauses", "75", "Faible", "Progressif."},
                            {"Repos", "0", "Faible", "Repos."}
                    };
                };
            }
        } else {
            // OBÉSITÉ – plans protecteurs adaptés par genre
            if (isMale) {
                return switch (week) {
                    case 1 -> new String[][]{
                            {"Marche lente", "25", "Faible", "Rythme conversationnel, focus régularité."},
                            {"Gymnastique sur chaise", "30", "Faible", "Mobilité articulaire sans impact."},
                            {"Repos", "0", "Faible", "Récupération nécessaire."},
                            {"Aquagym (Eau tiède)", "35", "Faible", "Portance de l'eau pour les articulations."},
                            {"Marche en extérieur", "20", "Faible", "Air frais, rythme doux."},
                            {"Respiration guidée", "15", "Faible", "Détente et gestion du souffle."},
                            {"Repos", "0", "Faible", "Repos."}
                    };
                    default -> new String[][]{
                            {"Marche fractionnée (5 min x 3)", "20", "Faible", "Petites sessions pour ménager le cœur."},
                            {"Aquagym tonique douce", "35", "Faible", "Renforcement musculaire en apesanteur."},
                            {"Repos", "0", "Faible", "Récupération."},
                            {"Vélo assis (Sans résistance)", "20", "Faible", "Mouvement fluide des jambes."},
                            {"Marche active lente", "25", "Faible", "Progression douce vers plus de durée."},
                            {"Étirements doux", "30", "Faible", "Souplesse et bien-être."},
                            {"Repos", "0", "Faible", "Repos."}
                    };
                };
            } else {
                return switch (week) {
                    case 1 -> new String[][]{
                            {"Marche aquatique", "30", "Faible", "Idéal pour le retour veineux et articulations."},
                            {"Yoga sur chaise", "25", "Faible", "Étirements et posture sans pression."},
                            {"Repos", "0", "Faible", "Récupération active."},
                            {"Marche très lente", "20", "Faible", "Focus sur la posture et la respiration."},
                            {"Aquagym douce", "30", "Faible", "Mouvements fluides dans l'eau."},
                            {"Relaxation / Méditation", "20", "Faible", "Gestion du stress et du corps."},
                            {"Repos", "0", "Faible", "Repos."}
                    };
                    default -> new String[][]{
                            {"Vélo aquatique lent", "25", "Faible", "Activité douce sans impact articulaire."},
                            {"Marche en parc", "25", "Faible", "Environnement apaisant."},
                            {"Pilates très doux", "30", "Faible", "Mobilisation de la colonne."},
                            {"Repos", "0", "Faible", "Repos."},
                            {"Étirements doux", "20", "Faible", "Souplesse générale."},
                            {"Marche lente", "20", "Faible", "Petite sortie."},
                            {"Repos", "0", "Faible", "Repos."}
                    };
                };
            }
        }
    }
    @Override
    @Transactional(readOnly = true)
    public List<ActivityRecommendationDto> generateActivityPlanByUserIdAndWeek(Long userId, int weekNumber, boolean ignoreInjuries) {
        log.info("Generating activity plan for user {}, week {}, ignoreInjuries={}", userId, weekNumber, ignoreInjuries);
        HealthProfile profile = healthProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Health profile not found for user id: " + userId));

        Double bmi = profile.getBmi();
        if (bmi == null) {
            throw new RuntimeException("Cannot generate activity plan: BMI is not calculated.");
        }

        // ----- Gestion des blessures (uniquement si ignoreInjuries == false) -----
        boolean hasActiveInjury = false, hasLowerBodyInjury = false, hasUpperBodyInjury = false, hasConcussion = false;
        if (!ignoreInjuries) {
            List<MedicalRecord> records = medicalRecordRepository.findByHealthProfileId(profile.getId());
            hasActiveInjury = records.stream().anyMatch(r ->
                    r.getRecoveryStatus() == RecoveryStatus.IN_PROGRESS ||
                            r.getRecoveryStatus() == RecoveryStatus.PENDING ||
                            r.getRecoveryStatus() == RecoveryStatus.COMPLICATED);
            hasLowerBodyInjury = records.stream().anyMatch(r ->
                    (r.getRecoveryStatus() == RecoveryStatus.IN_PROGRESS || r.getRecoveryStatus() == RecoveryStatus.PENDING) &&
                            (r.getInjuryType() == InjuryType.SPRAIN || r.getInjuryType() == InjuryType.FRACTURE ||
                                    r.getInjuryType() == InjuryType.STRESS_FRACTURE || r.getInjuryType() == InjuryType.LIGAMENT_TEAR));
            hasUpperBodyInjury = records.stream().anyMatch(r ->
                    (r.getRecoveryStatus() == RecoveryStatus.IN_PROGRESS || r.getRecoveryStatus() == RecoveryStatus.PENDING) &&
                            (r.getInjuryType() == InjuryType.MUSCLE_STRAIN || r.getInjuryType() == InjuryType.MUSCLE_TEAR ||
                                    r.getInjuryType() == InjuryType.DISLOCATION));
            hasConcussion = records.stream().anyMatch(r ->
                    r.getRecoveryStatus() != RecoveryStatus.COMPLETED && r.getInjuryType() == InjuryType.CONCUSSION);
        }

        List<ActivityRecommendationDto> plan = new ArrayList<>();

        if (!ignoreInjuries && hasConcussion) {
            return plan;
        }
        if (!ignoreInjuries && hasActiveInjury && hasLowerBodyInjury) {
            return plan;
        }
        if (!ignoreInjuries && hasActiveInjury && hasUpperBodyInjury) {
            return plan;
        }
        if (!ignoreInjuries && hasActiveInjury) {
            return plan;
        }

        int week = ((weekNumber - 1) % 4) + 1;
        tn.esprit._4se2.pi.Enum.Gender gender = profile.getGender() != null ? profile.getGender() : tn.esprit._4se2.pi.Enum.Gender.MALE;
        String[][] activities = getActivitiesForBmiAndWeek(bmi, week, gender);
        String[] jours = {"Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi", "Dimanche"};
        for (int i = 0; i < 7; i++) {
            plan.add(new ActivityRecommendationDto(jours[i], activities[i][0],
                    Integer.parseInt(activities[i][1]), activities[i][2], activities[i][3]));
        }
        return plan;
    }

    @Override
    public void sendDailyReport(Long userId) {
        log.info("Sending daily sports report for user {}", userId);
        List<ActivityRecommendationDto> plan = generateActivityPlanByUserId(userId);
        int today = LocalDate.now().getDayOfWeek().getValue();
        ActivityRecommendationDto todayActivity = plan.get((today - 1) % 7);

        String title = "⚡ Votre activité du jour : " + todayActivity.getDayOfWeek();
        String message = String.format("Aujourd'hui : %s (%d min). Intensité : %s. \nFocus : %s",
                todayActivity.getActivityName(), todayActivity.getDurationMinutes(),
                todayActivity.getIntensity(), todayActivity.getDescription());

        notificationService.createNotification(NotificationRequest.builder()
                .userId(userId)
                .title(title)
                .message(message)
                .type("DAILY_REPORT")
                .build());
    }

    @Override
    public void sendWeeklySummary(Long userId) {
        log.info("Sending weekly summary for user {}", userId);
        List<ActivityRecommendationDto> plan = generateActivityPlanByUserId(userId);
        int totalMinutes = plan.stream().mapToInt(ActivityRecommendationDto::getDurationMinutes).sum();
        
        HealthProfile profile = healthProfileRepository.findByUserId(userId).orElse(null);
        String category = profile != null ? profile.getBmiCategory() : "Athlète";

        String title = "📊 Votre Bilan Hebdomadaire";
        String message = String.format("Semaine terminée ! Volume total : %d min. Focus : %s. \nContinuez vos efforts !",
                totalMinutes, category);

        notificationService.createNotification(NotificationRequest.builder()
                .userId(userId)
                .title(title)
                .message(message)
                .type("WEEKLY_SUMMARY")
                .build());
    }
}