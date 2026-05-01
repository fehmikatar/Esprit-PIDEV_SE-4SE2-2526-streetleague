package tn.esprit._4se2.pi.services.DietPlan;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit._4se2.pi.dto.DietPlan.DietPlanRequest;
import tn.esprit._4se2.pi.dto.DietPlan.DietPlanResponse;
import tn.esprit._4se2.pi.entities.DietPlan;
import tn.esprit._4se2.pi.entities.HealthProfile;
import tn.esprit._4se2.pi.mappers.DietPlanMapper;
import tn.esprit._4se2.pi.repositories.DietPlanRepository;
import tn.esprit._4se2.pi.repositories.HealthProfileRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class DietPlanService implements IDietPlanService {

    private final DietPlanRepository dietPlanRepository;
    private final HealthProfileRepository healthProfileRepository;
    private final DietPlanMapper dietPlanMapper;

    @Override
    public DietPlanResponse createDietPlan(DietPlanRequest request) {
        HealthProfile hp = healthProfileRepository.findById(request.getHealthProfileId()).orElseThrow();
        DietPlan dietPlan = dietPlanMapper.toEntity(request);
        dietPlan.setHealthProfile(hp);
        if (dietPlan.getIsActive() == null) dietPlan.setIsActive(true);
        return dietPlanMapper.toResponse(dietPlanRepository.save(dietPlan));
    }

    @Override
    @Transactional(readOnly = true)
    public DietPlanResponse getDietPlanById(Long id) {
        return dietPlanRepository.findById(id).map(dietPlanMapper::toResponse).orElseThrow();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DietPlanResponse> getAllDietPlans() {
        return dietPlanRepository.findAll().stream().map(dietPlanMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DietPlanResponse> getDietPlansByHealthProfileId(Long hpId) {
        return dietPlanRepository.findByHealthProfileId(hpId).stream().map(dietPlanMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DietPlanResponse> getActiveDietPlans() {
        return dietPlanRepository.findByIsActiveTrue().stream().map(dietPlanMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DietPlanResponse> getActiveDietPlansByHealthProfile(Long hpId) {
        return dietPlanRepository.findByHealthProfileIdAndIsActiveTrue(hpId).stream().map(dietPlanMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    public DietPlanResponse updateDietPlan(Long id, DietPlanRequest request) {
        DietPlan existing = dietPlanRepository.findById(id).orElseThrow();
        dietPlanMapper.updateEntity(request, existing);
        return dietPlanMapper.toResponse(dietPlanRepository.save(existing));
    }

    @Override
    public void deleteDietPlan(Long id) {
        dietPlanRepository.deleteById(id);
    }

    @Override
    public void activateDietPlan(Long id) {
        DietPlan dp = dietPlanRepository.findById(id).orElseThrow();
        dp.setIsActive(true);
        dietPlanRepository.save(dp);
    }

    @Override
    public void deactivateDietPlan(Long id) {
        DietPlan dp = dietPlanRepository.findById(id).orElseThrow();
        dp.setIsActive(false);
        dietPlanRepository.save(dp);
    }

    @Override
    @Transactional(propagation = org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED)
    public List<Map<String, Object>> searchFoodCalories(String query) {
        String q = query.toLowerCase().trim();
        log.info("Searching calories for: {}", q);
        List<Map<String, Object>> results = new ArrayList<>();

        // 1. FatSecret Web Scraping
        try {
            String fatSecretUrl = "https://www.fatsecret.fr/calories-nutrition/search?q=" + URLEncoder.encode(q, StandardCharsets.UTF_8);
            Document doc = Jsoup.connect(fatSecretUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                    .timeout(10000)
                    .get();

            Elements resultElements = doc.select("table.generic.searchResult tr td.borderBottom");
            for (Element el : resultElements) {
                Element nameEl = el.selectFirst("a.prominent");
                Element detailsEl = el.selectFirst("div.smallText");

                if (nameEl != null && detailsEl != null) {
                    String name = nameEl.text();
                    String details = detailsEl.text();

                    java.util.regex.Matcher m = java.util.regex.Pattern.compile("Calories:\\s*(\\d+)\\s*kcal").matcher(details);
                    if (m.find()) {
                        int cals = Integer.parseInt(m.group(1));
                        Map<String, Object> food = new HashMap<>();
                        food.put("name", name + " (Scraped)");
                        food.put("calories", cals);
                        results.add(food);
                    }
                }
                if (results.size() >= 10) break;
            }
        } catch (Exception e) {
            log.error("FatSecret scraping error: {}", e.getMessage());
        }

        // 2. Open Food Facts API (Fallback/Additional)
        if (results.size() < 10) {
            try {
                String url = "https://world.openfoodfacts.org/cgi/search.pl?search_terms=" + URLEncoder.encode(q, StandardCharsets.UTF_8) + "&search_simple=1&action=process&json=1";
                String json = Jsoup.connect(url).ignoreContentType(true).userAgent("Mozilla/5.0").timeout(8000).execute().body();
                
                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                com.fasterxml.jackson.databind.JsonNode root = mapper.readTree(json);
                com.fasterxml.jackson.databind.JsonNode products = root.path("products");
                
                for (com.fasterxml.jackson.databind.JsonNode product : products) {
                    String name = product.path("product_name").asText("Inconnu");
                    int cals = product.path("nutriments").path("energy-kcal_100g").asInt(0);
                    if (cals > 0) {
                        // Avoid duplicates if possible, simply add
                        Map<String, Object> food = new HashMap<>();
                        food.put("name", name + " (Web)");
                        food.put("calories", cals);
                        results.add(food);
                    }
                    if (results.size() >= 15) break;
                }
            } catch (Exception e) {
                log.error("Open Food Facts API error: {}", e.getMessage());
            }
        }

        if (results.isEmpty()) {
            Map<String, Object> fallback = new HashMap<>();
            fallback.put("name", query + " (Valeur estimée)");
            fallback.put("calories", 100);
            results.add(fallback);
        }
        return results;
    }

    @Override
    @Transactional
    public DietPlanResponse generateRecommendedDietPlan(Long healthProfileId, String goal) {
        HealthProfile hp = healthProfileRepository.findById(healthProfileId)
            .orElseThrow(() -> new RuntimeException("HealthProfile not found"));

        if (hp.getWeight() == null || hp.getHeight() == null || hp.getAge() == null || hp.getGender() == null) {
            throw new RuntimeException("Données de santé insuffisantes pour la recommandation (poids, taille, âge ou genre manquant).");
        }

        // 1. Calcul du BMR (Mifflin-St Jeor)
        double bmr = (10 * hp.getWeight()) + (6.25 * hp.getHeight()) - (5 * hp.getAge());
        if (hp.getGender() == tn.esprit._4se2.pi.Enum.Gender.MALE) {
            bmr += 5;
        } else {
            bmr -= 161;
        }

        // 2. Facteur d'activité basé sur FitnessStatus
        double activityFactor = 1.375; // Moyen par défaut
        if (hp.getFitnessStatus() != null) {
            switch (hp.getFitnessStatus()) {
                case ACTIVE:
                    activityFactor = 1.55;
                    break;
                case LIMITED:
                case RECOVERING:
                    activityFactor = 1.375;
                    break;
                case INJURED:
                case RESTING:
                    activityFactor = 1.2;
                    break;
            }
        }
        
        double tdee = bmr * activityFactor;
        
        // 3. Objectif en fonction de l'IMC ou paramètre choisi
        Double bmi = hp.getBmi();
        double targetCalories;
        String goalText;
        
        // Détermination du but :
        String finalGoal = "maintain";
        if (goal != null && !goal.trim().isEmpty()) {
            finalGoal = goal.toLowerCase();
        } else {
            if (bmi < 18.5) finalGoal = "gain";
            else if (bmi >= 25) finalGoal = "loss";
        }

        // Variations de repas pour une recommandation différente chaque jour (pseudo-aléatoire basé sur la date)
        int daySeed = java.time.LocalDate.now().getDayOfYear();
        String mealSuggestions;

        if (finalGoal.equals("gain")) { 
            targetCalories = tdee + 500;
            goalText = "Prise de masse pour optimiser vos performances.";
            String[] breakfasts = {
                "Petit-déjeuner : flocons d'avoine, lait entier, beurre de cacahuète, banane, amandes",
                "Petit-déjeuner : 4 œufs brouillés, pain complet, fromage, jus d'orange frais",
                "Petit-déjeuner : pancakes protéinés, sirop d'érable, yaourt grec, noix"
            };
            String[] lunches = {
                "Déjeuner : riz complet, poulet rôti, avocat, légumes sautés, huile d'olive",
                "Déjeuner : pâtes au blé complet, bœuf haché maigre, sauce tomate maison, parmesan",
                "Déjeuner : quinoa, saumon grillé, patates douces rôties, brocolis"
            };
            String[] dinners = {
                "Dîner : saumon, quinoa, salade mixte, noix de pécan",
                "Dîner : steak de dinde, purée de pommes de terre, haricots verts",
                "Dîner : lentilles, riz, thon, salade d'épinards"
            };
            mealSuggestions = breakfasts[daySeed % 3] + "\n" + lunches[daySeed % 3] + "\n" + dinners[daySeed % 3] + "\nCollation : smoothie protéiné, oléagineux";
        } else if (finalGoal.equals("loss")) { 
            targetCalories = tdee - 500;
            goalText = "Perte de poids contrôlée pour réduire le surplus calorique.";
            String[] breakfasts = {
                "Petit-déjeuner : œufs brouillés, épinards, thé vert, pamplemousse",
                "Petit-déjeuner : yaourt nature 0%, fruits rouges, poignée d'amandes",
                "Petit-déjeuner : smoothie vert (épinards, pomme, concombre), 2 œufs durs"
            };
            String[] lunches = {
                "Déjeuner : blanc de poulet, brocolis vapeur, salade mixte, tomate",
                "Déjeuner : filet de poisson blanc, courgettes grillées, petite portion de quinoa",
                "Déjeuner : salade composée (thon, œuf, salade, concombre), vinaigrette allégée"
            };
            String[] dinners = {
                "Dîner : poisson blanc, haricots verts, soupe de légumes, carottes",
                "Dîner : soupe de potiron, blanc de dinde grillé",
                "Dîner : wok de légumes sans matière grasse, tofu grillé"
            };
            mealSuggestions = breakfasts[daySeed % 3] + "\n" + lunches[daySeed % 3] + "\n" + dinners[daySeed % 3] + "\nCollation : pomme, fromage blanc 0%";
        } else { 
            targetCalories = tdee;
            goalText = "Maintien du poids idéal et optimisation des performances.";
            String[] breakfasts = {
                "Petit-déjeuner : yaourt grec, fruits rouges, amandes, pain complet",
                "Petit-déjeuner : muesli sans sucre ajouté, lait demi-écrémé, kiwi",
                "Petit-déjeuner : 2 œufs au plat, pain aux céréales, café ou thé"
            };
            String[] lunches = {
                "Déjeuner : dinde, riz basmati, carottes rôties, lentilles",
                "Déjeuner : steak haché 5%, pâtes complètes, haricots verts",
                "Déjeuner : salade de riz, poulet, maïs, tomates cerises"
            };
            String[] dinners = {
                "Dîner : tofu ou poulet, quinoa, légumes variés, huile d'olive",
                "Dîner : filet de cabillaud, purée de chou-fleur, salade",
                "Dîner : omelette aux champignons, petite portion de frites au four, salade verte"
            };
            mealSuggestions = breakfasts[daySeed % 3] + "\n" + lunches[daySeed % 3] + "\n" + dinners[daySeed % 3] + "\nCollation : un fruit de saison, quelques noix";
        }

        int finalCalories = (int) Math.round(targetCalories);

        // 4. Création automatique du Plan Alimentaire
        DietPlan dietPlan = new DietPlan();
        dietPlan.setHealthProfile(hp);
        dietPlan.setPlanName("Recommandation du Jour (" + java.time.LocalDate.now() + ")");
        dietPlan.setDescription("Plan du jour (" + finalGoal + ") généré automatiquement basé sur vos métriques.");
        dietPlan.setDailyCalories(finalCalories);
        dietPlan.setMealSuggestions(mealSuggestions);
        dietPlan.setNutritionalGoals(goalText);
        dietPlan.setStartDate(java.time.LocalDate.now());
        dietPlan.setEndDate(java.time.LocalDate.now().plusMonths(1));
        dietPlan.setIsActive(true);
        dietPlan.setCreatedBy("Système Métier");

        DietPlan savedPlan = dietPlanRepository.save(dietPlan);
        return dietPlanMapper.toResponse(savedPlan);
    }
}