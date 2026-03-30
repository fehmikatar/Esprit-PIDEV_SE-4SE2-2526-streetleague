package tn.esprit._4se2.pi.entities;

public enum RecoveryStatus {
    PENDING,        // En attente de traitement
    IN_PROGRESS,    // Récupération en cours
    COMPLETED,      // Récupération terminée
    COMPLICATED,    // Complications
    REFERRED        // Référé à un spécialiste
}