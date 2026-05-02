package tn.esprit._4se2.pi.restcontrollers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tn.esprit._4se2.pi.Enum.RecoveryStatus;
import tn.esprit._4se2.pi.dto.MedicalRecord.MedicalRecordRequest;
import tn.esprit._4se2.pi.dto.MedicalRecord.MedicalRecordResponse;
import tn.esprit._4se2.pi.services.MedicalRecord.IMedicalRecordService;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/medical-records")
@RequiredArgsConstructor
public class MedicalRecordRestController {

    private final IMedicalRecordService medicalRecordService;

    @PreAuthorize("hasAnyRole('FIELD_OWNER', 'ADMIN')")
    @PostMapping
    public ResponseEntity<MedicalRecordResponse> createMedicalRecord(@Valid @RequestBody MedicalRecordRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(medicalRecordService.createMedicalRecord(request));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<MedicalRecordResponse> getMedicalRecordById(@PathVariable Long id) {
        return ResponseEntity.ok(medicalRecordService.getMedicalRecordById(id));
    }

    @PreAuthorize("hasAnyRole('FIELD_OWNER', 'ADMIN')")
    @GetMapping
    public ResponseEntity<List<MedicalRecordResponse>> getAllMedicalRecords() {
        return ResponseEntity.ok(medicalRecordService.getAllMedicalRecords());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/health-profile/{healthProfileId}")
    public ResponseEntity<List<MedicalRecordResponse>> getMedicalRecordsByHealthProfile(@PathVariable Long healthProfileId) {
        return ResponseEntity.ok(medicalRecordService.getMedicalRecordsByHealthProfileId(healthProfileId));
    }

    @PreAuthorize("hasAnyRole('FIELD_OWNER', 'ADMIN')")
    @GetMapping("/status/{status}")
    public ResponseEntity<List<MedicalRecordResponse>> getMedicalRecordsByStatus(@PathVariable RecoveryStatus status) {
        return ResponseEntity.ok(medicalRecordService.getMedicalRecordsByStatus(status));
    }

    @PreAuthorize("hasAnyRole('FIELD_OWNER', 'ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<MedicalRecordResponse> updateMedicalRecord(
            @PathVariable Long id,
            @Valid @RequestBody MedicalRecordRequest request) {
        return ResponseEntity.ok(medicalRecordService.updateMedicalRecord(id, request));
    }

    @PreAuthorize("hasAnyRole('FIELD_OWNER', 'ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMedicalRecord(@PathVariable Long id) {
        medicalRecordService.deleteMedicalRecord(id);
        return ResponseEntity.noContent().build();
    }
}