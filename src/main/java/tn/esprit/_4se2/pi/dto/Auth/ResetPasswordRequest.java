package tn.esprit._4se2.pi.dto.Auth;


public record ResetPasswordRequest(String token, String newPassword) {

}