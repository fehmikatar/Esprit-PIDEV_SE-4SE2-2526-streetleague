package tn.esprit._4se2.pi.services.MedicalRecord;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit._4se2.pi.dto.MedicalRecord.MedicalRecordRequest;
import tn.esprit._4se2.pi.dto.MedicalRecord.MedicalRecordResponse;
import tn.esprit._4se2.pi.entities.MedicalRecord;
import tn.esprit._4se2.pi.entities.HealthProfile;
import tn.esprit._4se2.pi.entities.Doctor;
import tn.esprit._4se2.pi.Enum.RecoveryStatus;
import tn.esprit._4se2.pi.mappers.MedicalRecordMapper;
import tn.esprit._4se2.pi.repositories.MedicalRecordRepository;
import tn.esprit._4se2.pi.repositories.HealthProfileRepository;
import tn.esprit._4se2.pi.repositories.DoctorRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MedicalRecordService implements IMedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;
    private final HealthProfileRepository healthProfileRepository;
    private final DoctorRepository doctorRepository;
    private final MedicalRecordMapper medicalRecordMapper;

    @Override
    public MedicalRecordResponse createMedicalRecord(MedicalRecordRequest request) {
        log.info("Creating medical record for health profile id: {}", request.getHealthProfileId());

        HealthProfile healthProfile = healthProfileRepository.findById(request.getHealthProfileId())
                .orElseThrow(() -> new RuntimeException("Health profile not found with id: " + request.getHealthProfileId()));

        MedicalRecord record = medicalRecordMapper.toEntity(request);
        record.setHealthProfile(healthProfile);

        if (request.getTreatedByDoctorId() != null) {
            Doctor doctor = doctorRepository.findById(request.getTreatedByDoctorId())
                    .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + request.getTreatedByDoctorId()));
            record.setTreatedBy(doctor);
        }

        MedicalRecord saved = medicalRecordRepository.save(record);
        log.info("Medical record created with id: {}", saved.getId());

        return medicalRecordMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public MedicalRecordResponse getMedicalRecordById(Long id) {
        log.info("Fetching medical record with id: {}", id);
        return medicalRecordRepository.findById(id)
                .map(medicalRecordMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Medical record not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicalRecordResponse> getAllMedicalRecords() {
        log.info("Fetching all medical records");
        return medicalRecordRepository.findAll()
                .stream()
                .map(medicalRecordMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicalRecordResponse> getMedicalRecordsByHealthProfileId(Long healthProfileId) {
        log.info("Fetching medical records for health profile id: {}", healthProfileId);
        return medicalRecordRepository.findByHealthProfileId(healthProfileId)
                .stream()
                .map(medicalRecordMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicalRecordResponse> getMedicalRecordsByStatus(RecoveryStatus status) {
        log.info("Fetching medical records with status: {}", status);
        return medicalRecordRepository.findByRecoveryStatus(status)
                .stream()
                .map(medicalRecordMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public MedicalRecordResponse updateMedicalRecord(Long id, MedicalRecordRequest request) {
        log.info("Updating medical record with id: {}", id);

        MedicalRecord existing = medicalRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Medical record not found with id: " + id));

        // If health profile changed
        if (!existing.getHealthProfile().getId().equals(request.getHealthProfileId())) {
            HealthProfile newHealthProfile = healthProfileRepository.findById(request.getHealthProfileId())
                    .orElseThrow(() -> new RuntimeException("Health profile not found with id: " + request.getHealthProfileId()));
            existing.setHealthProfile(newHealthProfile);
        }

        // If doctor changed
        if (request.getTreatedByDoctorId() != null) {
            if (existing.getTreatedBy() == null || !existing.getTreatedBy().getId().equals(request.getTreatedByDoctorId())) {
                Doctor newDoctor = doctorRepository.findById(request.getTreatedByDoctorId())
                        .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + request.getTreatedByDoctorId()));
                existing.setTreatedBy(newDoctor);
            }
        } else {
            existing.setTreatedBy(null);
        }

        medicalRecordMapper.updateEntity(request, existing);
        MedicalRecord updated = medicalRecordRepository.save(existing);
        log.info("Medical record updated with id: {}", id);

        return medicalRecordMapper.toResponse(updated);
    }

    @Override
    public void deleteMedicalRecord(Long id) {
        log.info("Deleting medical record with id: {}", id);
        if (!medicalRecordRepository.existsById(id)) {
            throw new RuntimeException("Medical record not found with id: " + id);
        }
        medicalRecordRepository.deleteById(id);
        log.info("Medical record deleted with id: {}", id);
    }
}