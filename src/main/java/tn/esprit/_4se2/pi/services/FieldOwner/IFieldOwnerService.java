package tn.esprit._4se2.pi.services.FieldOwner;

import tn.esprit._4se2.pi.dto.FieldOwnerRequest;
import tn.esprit._4se2.pi.dto.FieldOwnerResponse;
import java.util.List;

public interface IFieldOwnerService {
    FieldOwnerResponse createFieldOwner(FieldOwnerRequest request);
    FieldOwnerResponse getFieldOwnerById(Long id);
    List<FieldOwnerResponse> getAllFieldOwners();
    FieldOwnerResponse updateFieldOwner(Long id, FieldOwnerRequest request);
    void deleteFieldOwner(Long id);
}