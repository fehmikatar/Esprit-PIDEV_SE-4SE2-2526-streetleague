package tn.esprit._4se2.pi.utils;

import tn.esprit._4se2.pi.entities.Performance;

public final class XpCalculator {

    private XpCalculator() {}

    public static int calculateXpGained(Performance performance) {
        int xp = 0;
        xp += performance.getScore() * 20;               // 20 XP par but
        xp += performance.getAssists() * 15;             // 15 XP par passe décisive
        xp += (int) performance.getDistanceCovered();    // 1 XP par km
        xp += (int) (performance.getTimePlayed() * 0.5); // 0.5 XP par minute
        xp += (int) (performance.getRating() * 10);      // 10 XP par point de rating
        if (performance.getRating() > 8) xp += 50;
        if (performance.getRating() > 9) xp += 100;
        return xp;
    }
}