package tn.esprit._4se2.pi.Controllers;

import org.springframework.web.bind.annotation.*;
import tn.esprit._4se2.pi.Entities.Registration;
import tn.esprit._4se2.pi.Services.RegistrationService;

import java.util.List;

@RestController
@RequestMapping("/api/registrations")
public class RegistrationController {
    private final RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping
    public Registration register(@RequestBody Registration registration) {
        return registrationService.register(registration);
    }

    @GetMapping
    public List<Registration> getAll(@RequestParam Long competitionId) {
        return registrationService.getAll(competitionId);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        registrationService.delete(id);
    }

}
