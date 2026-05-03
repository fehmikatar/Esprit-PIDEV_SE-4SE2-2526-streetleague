package tn.esprit._4se2.pi.Enum;

public enum CallStatus {
    INITIATED,   // invite envoyée, pas encore répondue
    ONGOING,     // appel accepté et en cours
    ENDED,       // appel terminé normalement
    REJECTED,    // refusé par le destinataire
    MISSED,      // invitation sans réponse / caller annulé avant réponse
    FAILED       // erreur WebRTC
}
