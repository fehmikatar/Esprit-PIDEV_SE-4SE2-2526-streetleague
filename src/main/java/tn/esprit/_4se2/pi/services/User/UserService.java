package tn.esprit._4se2.pi.services.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit._4se2.pi.dto.User.UserRequest;
import tn.esprit._4se2.pi.dto.User.UserResponse;
import tn.esprit._4se2.pi.entities.User;
import tn.esprit._4se2.pi.mappers.UserMapper;
import tn.esprit._4se2.pi.repositories.UserRepository;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponse createUser(UserRequest request) {
        log.info("Creating user with email: {}", request.getEmail());

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("User with email " + request.getEmail() + " already exists");
        }

        User user = userMapper.toEntity(request);
        User savedUser = userRepository.save(user);
        log.info("User created successfully with id: {}", savedUser.getId());

        return userMapper.toResponse(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        log.info("Fetching user with id: {}", id);
        return userRepository.findById(id)
                .map(userMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        log.info("Fetching all users");
        return userRepository.findAll()
                .stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserByEmail(String email) {
        log.info("Fetching user with email: {}", email);
        return userRepository.findByEmail(email)
                .map(userMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getActiveUsers() {
        log.info("Fetching active users");
        return userRepository.findByIsActiveTrue()
                .stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse updateUser(Long id, UserRequest request) {
        log.info("Updating user with id: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        userMapper.updateEntity(request, user);
        User updatedUser = userRepository.save(user);
        log.info("User updated successfully with id: {}", id);

        return userMapper.toResponse(updatedUser);
    }

    @Override
    public void deleteUser(Long id) {
        log.info("Deleting user with id: {}", id);

        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }

        userRepository.deleteById(id);
        log.info("User deleted successfully with id: {}", id);
    }

    @Override
    public void deactivateUser(Long id) {
        log.info("Deactivating user with id: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        user.setIsActive(false);
        userRepository.save(user);
        log.info("User deactivated successfully with id: {}", id);
    }

    @Override
    public UserResponse updateProfile(Long id, String firstName, String lastName, String email, String phone) {
        log.info("Updating profile for user with id: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        userMapper.updateProfile(user, firstName, lastName, email, phone);
        User updatedUser = userRepository.save(user);
        log.info("Profile updated successfully for user with id: {}", id);

        return userMapper.toResponse(updatedUser);
    }

    @Override
    public String uploadProfileImage(Long id, byte[] imageData, String filename) {
        log.info("Uploading profile image for user with id: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        // Store image data in the database
        user.setProfileImageData(imageData);
        
        // Generate image URL
        String imageUrl = "/api/users/" + id + "/profile-image/content";
        user.setProfileImageUrl(imageUrl);
        
        userRepository.save(user);
        log.info("Profile image uploaded successfully for user with id: {}", id);

        return imageUrl;
    }

    @Override
    @Transactional(readOnly = true)
    public String getProfileImageUrl(Long id) {
        log.info("Fetching profile image URL for user with id: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        return user.getProfileImageUrl();
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] getProfileImageContent(Long id) {
        log.info("Fetching profile image content for user with id: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        byte[] imageData = user.getProfileImageData();
        if (imageData == null || imageData.length == 0) {
            throw new RuntimeException("No image data found for user with id: " + id);
        }

        return imageData;
    }
}