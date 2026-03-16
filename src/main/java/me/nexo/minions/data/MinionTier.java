package me.nexo.minions.data;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MinionTier {
    private int tier;
    private double delaySeconds; // Tiempo de farmeo (15s a 6s)
    private int storageSlots;    // Capacidad (1 a 15)

    public static MinionTier getStats(int tier) {
        // Escala matemática perfecta para 12 Tiers
        double delay = 15.0 - ((tier - 1) * 0.81);
        int slots = 1 + (tier > 1 ? (int) Math.ceil((tier - 1) * 1.27) : 0);
        return new MinionTier(tier, delay, slots);
    }
}