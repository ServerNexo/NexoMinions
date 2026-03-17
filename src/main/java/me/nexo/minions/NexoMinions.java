package me.nexo.minions;

import me.nexo.minions.commands.ComandoMinion;
import me.nexo.minions.manager.MinionManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class NexoMinions extends JavaPlugin {

    private MinionManager minionManager;

    @Override
    public void onEnable() {
        // 1. Inicializamos el cerebro de las abejas
        this.minionManager = new MinionManager(this);

        // 2. Registramos el comando /minion
        if (getCommand("minion") != null) {
            getCommand("minion").setExecutor(new ComandoMinion(this));
        }

        // 3. 🕒 El Reloj de las Abejas (Tick)
        // Esta tarea se ejecuta 1 vez por segundo (20 ticks) revisando qué abeja debe minar
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            if (minionManager != null) {
                minionManager.tickAll(System.currentTimeMillis());
            }
        }, 20L, 20L);

        getLogger().info("========================================");
        getLogger().info("🐝 NexoMinions V1.0 - ¡Sistema de Abejas Activado!");
        getLogger().info("🔗 Conectado exitosamente a NexoCore y AuraSkills.");
        getLogger().info("========================================");
    }

    @Override
    public void onDisable() {
        // Por si necesitas guardar datos al apagar el servidor
        getLogger().info("NexoMinions ha sido desactivado. ¡Abejas a dormir!");
    }

    // Getter para que otras clases puedan usar el Manager
    public MinionManager getMinionManager() {
        return minionManager;
    }
}