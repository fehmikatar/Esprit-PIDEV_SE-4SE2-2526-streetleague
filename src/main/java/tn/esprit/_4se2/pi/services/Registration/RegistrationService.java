package tn.esprit._4se2.pi.services.Registration;

import org.springframework.stereotype.Service;
import tn.esprit._4se2.pi.entities.Registration;
import tn.esprit._4se2.pi.repositories.RegistrationRepository;

import java.util.List;

@Service
public class RegistrationService {
    private final RegistrationRepository registrationRepository;

    public RegistrationService(RegistrationRepository registrationRepository) {
        this.registrationRepository = registrationRepository;
    }

    public Registration register(Registration registration) {
        return registrationRepository.save(registration);
    }

    public List<Registration> getAll(Long competitionId) {
        return registrationRepository.findByCompetitionId(competitionId);
    }

    public Registration getById(Long id) {
        return registrationRepository.findById(id).orElseThrow(() -> new RuntimeException("Registration not found"));
    }

    public void delete(Long id) {
        registrationRepository.deleteById(id);
    }
}
