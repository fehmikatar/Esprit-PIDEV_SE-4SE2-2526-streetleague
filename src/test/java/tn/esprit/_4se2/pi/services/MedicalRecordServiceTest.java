package tn.esprit._4se2.pi.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit._4se2.pi.Enum.RecoveryStatus;
import tn.esprit._4se2.pi.dto.MedicalRecord.MedicalRecordRequest;
import tn.esprit._4se2.pi.dto.MedicalRecord.MedicalRecordResponse;
import tn.esprit._4se2.pi.entities.HealthProfile;
import tn.esprit._4se2.pi.entities.MedicalRecord;
import tn.esprit._4se2.pi.mappers.MedicalRecordMapper;
import tn.esprit._4se2.pi.repositories.DoctorRepository;
import tn.esprit._4se2.pi.repositories.HealthProfileRepository;
import tn.esprit._4se2.pi.repositories.MedicalRecordRepository;
import tn.esprit._4se2.pi.services.MedicalRecord.MedicalRecordService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MedicalRecordServiceTest {

    @Mock private MedicalRecordRepository medicalRecordRepository;
    @Mock private HealthProfileRepository healthProfileRepository;
    @Mock private DoctorRepository doctorRepository;
    @Mock private MedicalRecordMapper medicalRecordMapper;
    @InjectMocks private MedicalRecordService medicalRecordService;

    @Test
    void getMedicalRecordsByHealthProfileId_ReturnsList() {
        Long profileId = 1L;
        HealthProfile profile = new HealthProfile();
        profile.setId(profileId);
        MedicalRecord record = new MedicalRecord();
        record.setId(10L);
        record.setHealthProfile(profile);

        when(medicalRecordRepository.findByHealthProfileId(profileId)).thenReturn(List.of(record));
        when(medicalRecordMapper.toResponse(any())).thenReturn(new MedicalRecordResponse());

        var result = medicalRecordService.getMedicalRecordsByHealthProfileId(profileId);
        assertThat(result).hasSize(1);
    }

    @Test
    void createMedicalRecord_Success() {
        MedicalRecordRequest request = new MedicalRecordRequest();
        request.setHealthProfileId(1L);
        request.setDiagnosis("Fracture");
        request.setInjuryDate(LocalDate.now());

        HealthProfile profile = new HealthProfile();
        profile.setId(1L);

        MedicalRecord record = new MedicalRecord();
        record.setId(20L);

        when(healthProfileRepository.findById(1L)).thenReturn(Optional.of(profile));
        when(medicalRecordMapper.toEntity(request)).thenReturn(record);
        when(medicalRecordRepository.save(any(MedicalRecord.class))).thenReturn(record);
        when(medicalRecordMapper.toResponse(record)).thenReturn(new MedicalRecordResponse());

        var response = medicalRecordService.createMedicalRecord(request);
        assertThat(response).isNotNull();
    }

    @Test
    void deleteMedicalRecord_WhenExists_Deletes() {
        Long id = 10L;
        when(medicalRecordRepository.existsById(id)).thenReturn(true);
        medicalRecordService.deleteMedicalRecord(id);
        verify(medicalRecordRepository, times(1)).deleteById(id);
    }

    @Test
    void deleteMedicalRecord_WhenNotFound_Throws() {
        Long id = 999L;
        when(medicalRecordRepository.existsById(id)).thenReturn(false);
        assertThatThrownBy(() -> medicalRecordService.deleteMedicalRecord(id))
                .isInstanceOf(RuntimeException.class);
    }
}