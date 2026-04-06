package tn.esprit._4se2.pi.Services;

import tn.esprit._4se2.pi.Entities.Registration;

import java.util.List;

public interface RegistrationServiceInterface {
    Registration register(Registration registration);
    List<Registration> getAll(Long competitionId);
    Registration getById(Long id);
    void delete(Long id);
}