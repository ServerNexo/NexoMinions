package me.nexo.minions;

import org.bukkit.plugin.java.JavaPlugin;

public class NexoMinions extends JavaPlugin {

    private static NexoMinions instance;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        // Inicializamos los managers (Fase 2)
        // minionManager = new MinionManager(this);
        // getServer().getPluginManager().registerEvents(new MinionInteractListener(this), this);

        getLogger().info("====================================");
        getLogger().info("🐝 NexoMinions ha sido activado");
        getLogger().info("Integraciones: Nexo, AuraSkills, Aurora");
        getLogger().info("====================================");
    }

    @Override
    public void onDisable() {
        // Aquí guardaremos todos los ItemDisplays (PDC) de forma segura antes de apagar
        // minionManager.guardarTodosLosMinions();
        getLogger().info("NexoMinions apagado de forma segura.");
    }

    public static NexoMinions getInstance() {
        return instance;
    }
}