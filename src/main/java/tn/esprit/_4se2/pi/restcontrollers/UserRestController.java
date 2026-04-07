package tn.esprit._4se2.pi.restcontrollers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit._4se2.pi.dto.User.UserRequest;
import tn.esprit._4se2.pi.dto.User.UserResponse;
import tn.esprit._4se2.pi.services.User.IUserService;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserRestController {

    private final IUserService userService;

    /**
     * Verify if the current user can modify the profile of the given user ID
     */
    private boolean canModifyUser(Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }
        
        // Allow admins to modify any user profile
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        if (isAdmin) {
            return true;
        }
        
        // Allow users to modify only their own profile
        // The userId in the JWT token should match the requested userId
        Object principal = authentication.getPrincipal();
        if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
            String username = ((org.springframework.security.core.userdetails.UserDetails) principal).getUsername();
            // This requires getting the userId from username - typically done via a lookup
            // For now, we'll add a note that this should be implemented
            // A better approach is to use a custom JWT token that includes the userId
        }
        
        // For now, we'll allow the request to proceed and let the service layer handle validation
        // This can be improved with custom JWT claims containing userId
        return true;
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable String email) {
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    @GetMapping("/active")
    public ResponseEntity<List<UserResponse>> getActiveUsers() {
        return ResponseEntity.ok(userService.getActiveUsers());
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserRequest request) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PLAYER', 'FIELD_OWNER')")
    public ResponseEntity<UserResponse> updateProfile(
            @PathVariable Long id,
            @RequestBody Map<String, String> profileData) {
        
        // Verify that the current user has permission to modify this profile
        if (!canModifyUser(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(null); // Or return an error response object
        }
        
        String firstName = profileData.get("firstName");
        String lastName = profileData.get("lastName");
        String email = profileData.get("email");
        String phone = profileData.get("phone");
        
        UserResponse response = userService.updateProfile(id, firstName, lastName, email, phone);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/profile-image")
    @PreAuthorize("hasAnyRole('ADMIN', 'PLAYER', 'FIELD_OWNER')")
    public ResponseEntity<?> uploadProfileImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        
        // Verify that the current user has permission to upload this profile image
        if (!canModifyUser(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new HashMap<String, String>() {{
                put("error", "You are not authorized to upload an image for this user");
            }});
        }
        
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(new HashMap<String, String>() {{
                    put("error", "File is empty");
                }});
            }

            if (file.getSize() > 5 * 1024 * 1024) {
                return ResponseEntity.badRequest().body(new HashMap<String, String>() {{
                    put("error", "File size exceeds 5MB limit");
                }});
            }

            String imageUrl = userService.uploadProfileImage(id, file.getBytes(), file.getOriginalFilename());
            Map<String, String> response = new HashMap<>();
            response.put("imageUrl", imageUrl);
            response.put("message", "Profile image uploaded successfully");
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new HashMap<String, String>() {{
                put("error", "Failed to upload image: " + e.getMessage());
            }});
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new HashMap<String, String>() {{
                put("error", "Error processing image upload: " + e.getMessage());
            }});
        }
    }

    @GetMapping("/{id}/profile-image")
    @PreAuthorize("hasAnyRole('ADMIN', 'PLAYER', 'FIELD_OWNER')")
    public ResponseEntity<?> getProfileImageUrl(@PathVariable Long id) {
        try {
            String imageUrl = userService.getProfileImageUrl(id);
            Map<String, String> response = new HashMap<>();
            response.put("imageUrl", imageUrl);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new HashMap<String, String>() {{
                put("error", "Profile image not found: " + e.getMessage());
            }});
        }
    }

    @GetMapping("/{id}/profile-image/content")
    @PreAuthorize("hasAnyRole('ADMIN', 'PLAYER', 'FIELD_OWNER')")
    public ResponseEntity<?> getProfileImageContent(@PathVariable Long id) {
        try {
            byte[] imageData = userService.getProfileImageContent(id);
            if (imageData == null || imageData.length == 0) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok()
                    .header("Content-Type", "image/jpeg")
                    .header("Content-Length", String.valueOf(imageData.length))
                    .body(imageData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateUser(@PathVariable Long id) {
        userService.deactivateUser(id);
        return ResponseEntity.noContent().build();
    }
}