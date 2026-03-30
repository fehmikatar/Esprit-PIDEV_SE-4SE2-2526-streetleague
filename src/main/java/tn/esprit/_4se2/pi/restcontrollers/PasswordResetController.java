package tn.esprit._4se2.pi.restcontrollers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit._4se2.pi.dto.Auth.ForgotPasswordRequest;
import tn.esprit._4se2.pi.dto.Auth.ForgotPasswordResponse;
import tn.esprit._4se2.pi.dto.Auth.ResetPasswordRequest;
import tn.esprit._4se2.pi.dto.Auth.ResetPasswordResponse;
import tn.esprit._4se2.pi.dto.Auth.ValidateTokenResponse;
import tn.esprit._4se2.pi.services.Auth.PasswordResetService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    @PostMapping("/forgot-password")
    public ResponseEntity<ForgotPasswordResponse> forgotPassword(
            @RequestBody ForgotPasswordRequest req) {
        passwordResetService.requestPasswordReset(req.email());
        return ResponseEntity.ok(new ForgotPasswordResponse(
                "Si cet email existe, un lien de réinitialisation a été envoyé."));
    }

    @GetMapping("/validate-token")
    public ResponseEntity<ValidateTokenResponse> validateToken(
            @RequestParam String token) {
        boolean valid = passwordResetService.validateToken(token);
        return ResponseEntity.ok(new ValidateTokenResponse(valid));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ResetPasswordResponse> resetPassword(
            @RequestBody ResetPasswordRequest req) {
        passwordResetService.resetPassword(req.token(), req.newPassword());
        return ResponseEntity.ok(new ResetPasswordResponse(
                "Mot de passe réinitialisé avec succès."));
    }
}