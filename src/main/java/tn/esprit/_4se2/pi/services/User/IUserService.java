package tn.esprit._4se2.pi.services.User;

import tn.esprit._4se2.pi.dto.UserRequest;
import tn.esprit._4se2.pi.dto.UserResponse;
import java.util.List;

public interface IUserService {
    UserResponse createUser(UserRequest request);
    UserResponse getUserById(Long id);
    List<UserResponse> getAllUsers();
    UserResponse getUserByEmail(String email);
    List<UserResponse> getActiveUsers();
    UserResponse updateUser(Long id, UserRequest request);
    void deleteUser(Long id);
    void deactivateUser(Long id);
}