package tn.esprit.se2.laakommanel.pi.entites;

public enum RecoveryStatus {
    PENDING,        // En attente de traitement
    IN_PROGRESS,    // Récupération en cours
    COMPLETED,      // Récupération terminée
    COMPLICATED,    // Complications
    REFERRED        // Référé à un spécialiste
}