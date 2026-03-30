package tn.esprit._4se2.pi.restcontrollers;

import io.swagger.v3.oas.annotations.tags.Tag;
import tn.esprit._4se2.pi.entities.InjuryType;
import tn.esprit._4se2.pi.entities.MedicalRecord;
import tn.esprit._4se2.pi.entities.RecoveryStatus;
import tn.esprit._4se2.pi.dto.MedicalRecordRequest;
import tn.esprit._4se2.pi.dto.MedicalRecordResponse;
import tn.esprit._4se2.pi.services.IMedicalRecordService;
import tn.esprit._4se2.pi.mappers.MedicalRecordMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/medical-records")
@RequiredArgsConstructor
@Tag(name = "Medical Records", description = "📋 Medical history and diagnoses")
public class MedicalRecordRestController {

    private final IMedicalRecordService medicalRecordService;
    private final MedicalRecordMapper medicalRecordMapper;

    @GetMapping
    public ResponseEntity<List<MedicalRecordResponse>> getAllMedicalRecords() {
        List<MedicalRecord> records = medicalRecordService.getAllMedicalRecords();
        List<MedicalRecordResponse> responses = records.stream()
                .map(medicalRecordMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicalRecordResponse> getMedicalRecordById(@PathVariable Long id) {
        MedicalRecord record = medicalRecordService.getMedicalRecordById(id);
        MedicalRecordResponse response = medicalRecordMapper.toResponse(record);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<MedicalRecordResponse> createMedicalRecord(@RequestBody MedicalRecordRequest request) {
        MedicalRecord record = medicalRecordMapper.toEntity(request);
        MedicalRecord created = medicalRecordService.createMedicalRecord(record);
        MedicalRecordResponse response = medicalRecordMapper.toResponse(created);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MedicalRecordResponse> updateMedicalRecord(@PathVariable Long id, @RequestBody MedicalRecordRequest request) {
        MedicalRecord record = medicalRecordMapper.toEntity(request);
        MedicalRecord updated = medicalRecordService.updateMedicalRecord(id, record);
        MedicalRecordResponse response = medicalRecordMapper.toResponse(updated);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMedicalRecord(@PathVariable Long id) {
        medicalRecordService.deleteMedicalRecord(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/health-profile/{healthProfileId}")
    public ResponseEntity<MedicalRecordResponse> getMedicalRecordByHealthProfile(@PathVariable Long healthProfileId) {
        MedicalRecord record = medicalRecordService.getMedicalRecordByHealthProfile(healthProfileId);
        MedicalRecordResponse response = medicalRecordMapper.toResponse(record);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/injury-type/{injuryType}")
    public ResponseEntity<List<MedicalRecordResponse>> getByInjuryType(@PathVariable InjuryType injuryType) {
        List<MedicalRecord> records = medicalRecordService.getMedicalRecordsByInjuryType(injuryType);
        List<MedicalRecordResponse> responses = records.stream()
                .map(medicalRecordMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/recovery-status/{status}")
    public ResponseEntity<List<MedicalRecordResponse>> getByRecoveryStatus(@PathVariable RecoveryStatus status) {
        List<MedicalRecord> records = medicalRecordService.getMedicalRecordsByRecoveryStatus(status);
        List<MedicalRecordResponse> responses = records.stream()
                .map(medicalRecordMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<MedicalRecordResponse>> getByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        List<MedicalRecord> records = medicalRecordService.getMedicalRecordsByDateRange(start, end);
        List<MedicalRecordResponse> responses = records.stream()
                .map(medicalRecordMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/health-profile/{healthProfileId}/injury-type/{injuryType}")
    public ResponseEntity<MedicalRecordResponse> getByHealthProfileAndInjuryType(
            @PathVariable Long healthProfileId,
            @PathVariable InjuryType injuryType) {
        MedicalRecord record = medicalRecordService.getMedicalRecordByHealthProfileAndInjuryType(healthProfileId, injuryType);
        MedicalRecordResponse response = medicalRecordMapper.toResponse(record);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<List<MedicalRecordResponse>> searchByDiagnosis(@RequestParam String keyword) {
        List<MedicalRecord> records = medicalRecordService.searchMedicalRecordsByDiagnosis(keyword);
        List<MedicalRecordResponse> responses = records.stream()
                .map(medicalRecordMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<MedicalRecordResponse>> getByDoctor(@PathVariable Long doctorId) {
        List<MedicalRecord> records = medicalRecordService.getMedicalRecordsByDoctor(doctorId);
        List<MedicalRecordResponse> responses = records.stream()
                .map(medicalRecordMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/requires-followup")
    public ResponseEntity<List<MedicalRecordResponse>> getRequiringFollowUp() {
        List<MedicalRecord> records = medicalRecordService.getMedicalRecordsRequiringFollowUp();
        List<MedicalRecordResponse> responses = records.stream()
                .map(medicalRecordMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/active")
    public ResponseEntity<List<MedicalRecordResponse>> getActiveRecords() {
        List<MedicalRecord> records = medicalRecordService.getActiveMedicalRecords();
        List<MedicalRecordResponse> responses = records.stream()
                .map(medicalRecordMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }
}