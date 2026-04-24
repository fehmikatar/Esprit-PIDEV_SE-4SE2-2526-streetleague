package tn.esprit._4se2.pi.services.User;

import tn.esprit._4se2.pi.dto.User.UserRequest;
import tn.esprit._4se2.pi.dto.User.UserResponse;
import java.util.List;

public interface IUserService {
    UserResponse createUser(UserRequest request);
    UserResponse getUserById(Long id);
    List<UserResponse> getAllUsers();
    UserResponse getUserByEmail(String email);
    List<UserResponse> getActiveUsers();
    UserResponse updateUser(Long id, UserRequest request);
    UserResponse updateProfile(Long id, String firstName, String lastName, String email, String phone);
    String uploadProfileImage(Long id, byte[] imageData, String filename);
    String getProfileImageUrl(Long id);
    byte[] getProfileImageContent(Long id);
    void deleteUser(Long id);
    void deactivateUser(Long id);
}