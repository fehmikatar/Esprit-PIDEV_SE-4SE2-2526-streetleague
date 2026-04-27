package tn.esprit._4se2.pi.services.Auth;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendPasswordResetEmail(String toEmail, String resetLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Réinitialisation de votre mot de passe");
        message.setText("""
                Bonjour,
                
                Vous avez demandé une réinitialisation de mot de passe.
                Cliquez sur ce lien (valable 15 minutes) :
                
                %s
                
                Si vous n'êtes pas à l'origine de cette demande, ignorez cet email.
                """.formatted(resetLink));

        mailSender.send(message);
    }

    public void sendLowStockAlertEmail(String toEmail, String productName, Integer stock) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("🔥 Alerte Stock : Votre produit favori est bientôt épuisé !");
        message.setText("""
                Bonjour,
                
                Nous vous informons qu'un produit que vous avez ajouté à vos favoris est presque en rupture de stock.
                
                Produit : %s
                Quantité restante : %d
                
                Profitez vite de l'occasion pour l'acheter avant la rupture de stock définitive !
                
                Cordialement,
                L'équipe StreetLeague
                """.formatted(productName, stock));

        mailSender.send(message);
    }
}