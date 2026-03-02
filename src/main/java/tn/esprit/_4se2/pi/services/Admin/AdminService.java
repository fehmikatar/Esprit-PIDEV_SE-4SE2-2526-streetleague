package tn.esprit._4se2.pi.services.Admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit._4se2.pi.dto.AdminRequest;
import tn.esprit._4se2.pi.dto.AdminResponse;
import tn.esprit._4se2.pi.entities.Admin;
import tn.esprit._4se2.pi.mappers.AdminMapper;
import tn.esprit._4se2.pi.repositories.AdminRepository;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AdminService implements IAdminService {

    private final AdminRepository adminRepository;
    private final AdminMapper adminMapper;

    @Override
    public AdminResponse createAdmin(AdminRequest request) {
        log.info("Creating admin with email: {}", request.getEmail());

        Admin admin = adminMapper.toEntity(request);
        Admin savedAdmin = adminRepository.save(admin);
        log.info("Admin created successfully with id: {}", savedAdmin.getId());

        return adminMapper.toResponse(savedAdmin);
    }

    @Override
    @Transactional(readOnly = true)
    public AdminResponse getAdminById(Long id) {
        log.info("Fetching admin with id: {}", id);
        return adminRepository.findById(id)
                .map(adminMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Admin not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdminResponse> getAllAdmins() {
        log.info("Fetching all admins");
        return adminRepository.findAll()
                .stream()
                .map(adminMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdminResponse> getAdminsByRole(String role) {
        log.info("Fetching admins with role: {}", role);
        return adminRepository.findByAdminRole(role)
                .stream()
                .map(adminMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public AdminResponse updateAdmin(Long id, AdminRequest request) {
        log.info("Updating admin with id: {}", id);

        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Admin not found with id: " + id));

        adminMapper.updateEntity(request, admin);
        Admin updatedAdmin = adminRepository.save(admin);
        log.info("Admin updated successfully with id: {}", id);

        return adminMapper.toResponse(updatedAdmin);
    }

    @Override
    public void deleteAdmin(Long id) {
        log.info("Deleting admin with id: {}", id);

        if (!adminRepository.existsById(id)) {
            throw new RuntimeException("Admin not found with id: " + id);
        }

        adminRepository.deleteById(id);
        log.info("Admin deleted successfully with id: {}", id);
    }
}