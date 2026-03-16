package me.nexo.minions.data;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MinionTier {
    private int tier;
    private double delaySeconds; // Cada cuántos segundos genera un ítem
    private int storageSlots;    // Espacio base del minion
    private String requiredNexoItemToUpgrade; // Ej: "enchanted_cobblestone"
    private int upgradeCost;     // Cantidad de ese ítem requerida
    private int requiredCollectionLevel; // Nivel de AuroraCollections necesario

    /**
     * Devuelve una configuración estándar basada en el Tier.
     * Tier 1: 15s delay, 1 slot.
     * Tier 12: 6s delay, 15 slots.
     */
    public static MinionTier getStandardTier(int tier, String nexoUpgradeItem) {
        double delay = 15.0 - ((tier - 1) * 0.81); // Escala lineal de 15s a 6s
        int slots = 1 + (tier > 1 ? (int) Math.ceil((tier - 1) * 1.27) : 0); // Escala a 15 slots

        // El costo se duplica por cada nivel (ej. 32, 64, 128, 256...)
        int cost = (int) (16 * Math.pow(2, tier - 1));

        // Colección requerida para craftearlo (Tier / 2)
        int reqCollection = Math.max(1, tier / 2);

        return new MinionTier(tier, delay, slots, nexoUpgradeItem, cost, reqCollection);
    }
}