package tn.esprit._4se2.pi.services.Doctor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit._4se2.pi.dto.Doctor.DoctorRequest;
import tn.esprit._4se2.pi.dto.Doctor.DoctorResponse;
import tn.esprit._4se2.pi.entities.Doctor;
import tn.esprit._4se2.pi.mappers.DoctorMapper;
import tn.esprit._4se2.pi.repositories.DoctorRepository;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class DoctorService implements IDoctorService {

    private final DoctorRepository doctorRepository;
    private final DoctorMapper doctorMapper;

    @Override
    public DoctorResponse createDoctor(DoctorRequest request) {
        log.info("Creating doctor with email: {}", request.getEmail());

        if (doctorRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Doctor with email " + request.getEmail() + " already exists");
        }
        if (doctorRepository.findByLicenseNumber(request.getLicenseNumber()).isPresent()) {
            throw new RuntimeException("Doctor with license number " + request.getLicenseNumber() + " already exists");
        }

        Doctor doctor = doctorMapper.toEntity(request);
        Doctor saved = doctorRepository.save(doctor);
        log.info("Doctor created with id: {}", saved.getId());

        return doctorMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public DoctorResponse getDoctorById(Long id) {
        log.info("Fetching doctor with id: {}", id);
        return doctorRepository.findById(id)
                .map(doctorMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DoctorResponse> getAllDoctors() {
        log.info("Fetching all doctors");
        return doctorRepository.findAll()
                .stream()
                .map(doctorMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public DoctorResponse getDoctorByEmail(String email) {
        log.info("Fetching doctor with email: {}", email);
        return doctorRepository.findByEmail(email)
                .map(doctorMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Doctor not found with email: " + email));
    }

    @Override
    @Transactional(readOnly = true)
    public DoctorResponse getDoctorByLicenseNumber(String licenseNumber) {
        log.info("Fetching doctor with license number: {}", licenseNumber);
        return doctorRepository.findByLicenseNumber(licenseNumber)
                .map(doctorMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Doctor not found with license number: " + licenseNumber));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DoctorResponse> getDoctorsBySpecialty(String specialty) {
        log.info("Fetching doctors with specialty: {}", specialty);
        return doctorRepository.findBySpecialty(specialty)
                .stream()
                .map(doctorMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DoctorResponse> searchDoctors(String query) {
        log.info("Searching doctors with query: {}", query);
        return doctorRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(query, query)
                .stream()
                .map(doctorMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public DoctorResponse updateDoctor(Long id, DoctorRequest request) {
        log.info("Updating doctor with id: {}", id);

        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + id));

        // Check email uniqueness if changed
        if (!doctor.getEmail().equals(request.getEmail()) &&
                doctorRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Doctor with email " + request.getEmail() + " already exists");
        }
        // Check license number uniqueness if changed
        if (!doctor.getLicenseNumber().equals(request.getLicenseNumber()) &&
                doctorRepository.findByLicenseNumber(request.getLicenseNumber()).isPresent()) {
            throw new RuntimeException("Doctor with license number " + request.getLicenseNumber() + " already exists");
        }

        doctorMapper.updateEntity(request, doctor);
        Doctor updated = doctorRepository.save(doctor);
        log.info("Doctor updated with id: {}", id);

        return doctorMapper.toResponse(updated);
    }

    @Override
    public void deleteDoctor(Long id) {
        log.info("Deleting doctor with id: {}", id);
        if (!doctorRepository.existsById(id)) {
            throw new RuntimeException("Doctor not found with id: " + id);
        }
        doctorRepository.deleteById(id);
        log.info("Doctor deleted with id: {}", id);
    }
}