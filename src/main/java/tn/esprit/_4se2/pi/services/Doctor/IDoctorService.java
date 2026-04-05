package tn.esprit._4se2.pi.services.Doctor;

import tn.esprit._4se2.pi.dto.Doctor.DoctorRequest;
import tn.esprit._4se2.pi.dto.Doctor.DoctorResponse;
import java.util.List;

public interface IDoctorService {
    DoctorResponse createDoctor(DoctorRequest request);
    DoctorResponse getDoctorById(Long id);
    List<DoctorResponse> getAllDoctors();
    DoctorResponse getDoctorByEmail(String email);
    DoctorResponse getDoctorByLicenseNumber(String licenseNumber);
    List<DoctorResponse> getDoctorsBySpecialty(String specialty);
    List<DoctorResponse> searchDoctors(String query);
    DoctorResponse updateDoctor(Long id, DoctorRequest request);
    void deleteDoctor(Long id);
}