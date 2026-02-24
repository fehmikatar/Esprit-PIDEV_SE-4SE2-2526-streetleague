package tn.esprit._4se2.pi.services.FieldOwner;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit._4se2.pi.dto.FieldOwnerRequest;
import tn.esprit._4se2.pi.dto.FieldOwnerResponse;
import tn.esprit._4se2.pi.entities.FieldOwner;
import tn.esprit._4se2.pi.mappers.FieldOwnerMapper;
import tn.esprit._4se2.pi.repositories.FieldOwnerRepository;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class FieldOwnerService implements IFieldOwnerService {

    private final FieldOwnerRepository fieldOwnerRepository;
    private final FieldOwnerMapper fieldOwnerMapper;

    @Override
    public FieldOwnerResponse createFieldOwner(FieldOwnerRequest request) {
        log.info("Creating field owner with business name: {}", request.getBusinessName());

        if (fieldOwnerRepository.findByBusinessLicense(request.getBusinessLicense()).isPresent()) {
            throw new RuntimeException("Field owner with license " + request.getBusinessLicense() + " already exists");
        }

        FieldOwner fieldOwner = fieldOwnerMapper.toEntity(request);
        FieldOwner savedFieldOwner = fieldOwnerRepository.save(fieldOwner);
        log.info("Field owner created successfully with id: {}", savedFieldOwner.getId());

        return fieldOwnerMapper.toResponse(savedFieldOwner);
    }

    @Override
    @Transactional(readOnly = true)
    public FieldOwnerResponse getFieldOwnerById(Long id) {
        log.info("Fetching field owner with id: {}", id);
        return fieldOwnerRepository.findById(id)
                .map(fieldOwnerMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Field owner not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<FieldOwnerResponse> getAllFieldOwners() {
        log.info("Fetching all field owners");
        return fieldOwnerRepository.findAll()
                .stream()
                .map(fieldOwnerMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public FieldOwnerResponse updateFieldOwner(Long id, FieldOwnerRequest request) {
        log.info("Updating field owner with id: {}", id);

        FieldOwner fieldOwner = fieldOwnerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Field owner not found with id: " + id));

        fieldOwnerMapper.updateEntity(request, fieldOwner);
        FieldOwner updatedFieldOwner = fieldOwnerRepository.save(fieldOwner);
        log.info("Field owner updated successfully with id: {}", id);

        return fieldOwnerMapper.toResponse(updatedFieldOwner);
    }

    @Override
    public void deleteFieldOwner(Long id) {
        log.info("Deleting field owner with id: {}", id);

        if (!fieldOwnerRepository.existsById(id)) {
            throw new RuntimeException("Field owner not found with id: " + id);
        }

        fieldOwnerRepository.deleteById(id);
        log.info("Field owner deleted successfully with id: {}", id);
    }
}