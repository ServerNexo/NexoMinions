package me.nexo.minions;

import me.nexo.minions.manager.MinionManager;
import org.bukkit.plugin.java.JavaPlugin;

public class NexoMinions extends JavaPlugin {

    private static NexoMinions instance;
    private MinionManager minionManager;

    @Override
    public void onEnable() {
        instance = this;

        // Iniciamos el motor de minions
        minionManager = new MinionManager(this);

        getLogger().info("====================================");
        getLogger().info("🐝 NexoMinions ENGINE Activado");
        getLogger().info("Integraciones NATIVAS: Nexo, AuraSkills, Aurora");
        getLogger().info("Arquitectura: ItemDisplay (1.21.11+)");
        getLogger().info("====================================");
    }

    @Override
    public void onDisable() {
        getLogger().info("NexoMinions apagado correctamente.");
    }

    public static NexoMinions getInstance() {
        return instance;
    }

    public MinionManager getMinionManager() {
        return minionManager;
    }
}