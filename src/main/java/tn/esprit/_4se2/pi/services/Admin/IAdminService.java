package tn.esprit._4se2.pi.services.Admin;

import tn.esprit._4se2.pi.dto.AdminRequest;
import tn.esprit._4se2.pi.dto.AdminResponse;
import java.util.List;

public interface IAdminService {
    AdminResponse createAdmin(AdminRequest request);
    AdminResponse getAdminById(Long id);
    List<AdminResponse> getAllAdmins();
    List<AdminResponse> getAdminsByRole(String role);
    AdminResponse updateAdmin(Long id, AdminRequest request);
    void deleteAdmin(Long id);
}