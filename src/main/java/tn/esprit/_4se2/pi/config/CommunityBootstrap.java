package tn.esprit._4se2.pi.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import tn.esprit._4se2.pi.repositories.CategoryRepository;
import tn.esprit._4se2.pi.services.Community.CommunityService;

@Slf4j
@Component
@Order(1)
@RequiredArgsConstructor
public class CommunityBootstrap implements ApplicationRunner {

    private final CategoryRepository categoryRepository;
    private final CommunityService communityService;

    @Override
    public void run(ApplicationArguments args) {
        categoryRepository.findAll().forEach(communityService::ensureCommunityForCategory);
        log.info("Community bootstrap completed for existing categories.");
    }
}