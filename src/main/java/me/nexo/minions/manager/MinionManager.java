package me.nexo.minions.manager;

import com.nexomc.nexo.api.NexoItems;
import me.nexo.minions.NexoMinions;
import me.nexo.minions.data.MinionKeys;
import me.nexo.minions.data.MinionType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MinionManager {

    private final NexoMinions plugin;
    private final ConcurrentHashMap<UUID, ActiveMinion> minionsActivos = new ConcurrentHashMap<>();

    public MinionManager(NexoMinions plugin) {
        this.plugin = plugin;
        MinionKeys.init(plugin);
        iniciarTicker();
    }

    // Método para crear un minion en el mundo (Podremos llamarlo desde un comando de pruebas)
    public void spawnMinion(Location loc, UUID ownerId, MinionType type, int tier) {
        loc.getWorld().spawn(loc, ItemDisplay.class, display -> {
            var nexoItemBuilder = NexoItems.itemFromId(type.getNexoModelID());
            if (nexoItemBuilder != null) display.setItemStack(nexoItemBuilder.build());

            display.setBillboard(ItemDisplay.Billboard.FIXED);
            display.setInvulnerable(true);

            var pdc = display.getPersistentDataContainer();
            pdc.set(MinionKeys.OWNER, PersistentDataType.STRING, ownerId.toString());
            pdc.set(MinionKeys.TYPE, PersistentDataType.STRING, type.name());
            pdc.set(MinionKeys.TIER, PersistentDataType.INTEGER, tier);
            pdc.set(MinionKeys.NEXT_ACTION, PersistentDataType.LONG, System.currentTimeMillis());

            minionsActivos.put(display.getUniqueId(), new ActiveMinion(display, ownerId, type, tier, System.currentTimeMillis()));
        });
    }

    private void iniciarTicker() {
        // Corre de forma asíncrona para NUNCA dar lag al servidor
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            long ahora = System.currentTimeMillis();
            for (ActiveMinion minion : minionsActivos.values()) {
                if (minion.getEntity().isDead() || !minion.getEntity().isValid()) {
                    minionsActivos.remove(minion.getEntity().getUniqueId());
                    continue;
                }
                // Si le toca dar un ítem (spawnear item en el mundo), debemos volver al hilo principal
                Bukkit.getScheduler().runTask(plugin, () -> minion.tick(ahora));
            }
        }, 1L, 1L);
    }
}