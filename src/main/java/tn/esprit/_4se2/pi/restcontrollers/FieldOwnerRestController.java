package tn.esprit._4se2.pi.restcontrollers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit._4se2.pi.dto.FieldOwnerRequest;
import tn.esprit._4se2.pi.dto.FieldOwnerResponse;
import tn.esprit._4se2.pi.services.FieldOwner.IFieldOwnerService;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/field-owners")
@RequiredArgsConstructor
public class FieldOwnerRestController {

    private final IFieldOwnerService fieldOwnerService;

    @PostMapping
    public ResponseEntity<FieldOwnerResponse> createFieldOwner(@Valid @RequestBody FieldOwnerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(fieldOwnerService.createFieldOwner(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FieldOwnerResponse> getFieldOwnerById(@PathVariable Long id) {
        return ResponseEntity.ok(fieldOwnerService.getFieldOwnerById(id));
    }

    @GetMapping
    public ResponseEntity<List<FieldOwnerResponse>> getAllFieldOwners() {
        return ResponseEntity.ok(fieldOwnerService.getAllFieldOwners());
    }

    @PutMapping("/{id}")
    public ResponseEntity<FieldOwnerResponse> updateFieldOwner(
            @PathVariable Long id,
            @Valid @RequestBody FieldOwnerRequest request) {
        return ResponseEntity.ok(fieldOwnerService.updateFieldOwner(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFieldOwner(@PathVariable Long id) {
        fieldOwnerService.deleteFieldOwner(id);
        return ResponseEntity.noContent().build();
    }
}