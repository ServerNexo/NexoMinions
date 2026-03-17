package me.nexo.minions.data;

public enum MinionTier {
    TIER_1(1, 15.0), // Nivel 1: Tarda 15 segundos en minar
    TIER_2(2, 12.0), // Nivel 2: Tarda 12 segundos
    TIER_3(3, 9.0),  // Nivel 3: Tarda 9 segundos
    TIER_4(4, 6.0),  // Nivel 4: Tarda 6 segundos
    TIER_5(5, 3.0);  // Nivel 5 (Máximo): ¡Súper rápido, cada 3 segundos!

    private final int level;
    private final double delaySeconds;

    MinionTier(int level, double delaySeconds) {
        this.level = level;
        this.delaySeconds = delaySeconds;
    }

    public int getLevel() {
        return level;
    }

    public double getDelaySeconds() {
        return delaySeconds;
    }

    // 🌟 ¡Este es el método mágico que le faltaba a tu código!
    public static MinionTier getStats(int tierLevel) {
        for (MinionTier tier : values()) {
            if (tier.getLevel() == tierLevel) {
                return tier;
            }
        }
        return TIER_1; // Si por alguna razón no encuentra el nivel, asume que es Nivel 1
    }
}