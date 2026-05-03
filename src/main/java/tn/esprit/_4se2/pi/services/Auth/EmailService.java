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
        message.setSubject("\uD83D\uDD25 Alerte Stock : Votre produit favori est bient\u00F4t \u00E9puis\u00E9 !");
        
        String content = """
                Bonjour,
                
                Nous vous informons qu'un produit que vous avez ajout\u00E9 \u00E0 vos favoris est presque en rupture de stock.
                
                Produit : %s
                Quantit\u00E9 restante : %d
                
                Profitez vite de l'occasion pour l'acheter avant la rupture de stock d\u00E9finitive !
                
                Cordialement,
                L'\u00E9quipe StreetLeague
                """.formatted(productName, stock);
        
        message.setText(content);
        mailSender.send(message);
    }
}
