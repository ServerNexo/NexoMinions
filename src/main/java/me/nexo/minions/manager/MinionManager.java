package me.nexo.minions.manager;

import com.nexomc.nexo.api.NexoItems;
import me.nexo.minions.NexoMinions;
import me.nexo.minions.data.MinionKeys;
import me.nexo.minions.data.MinionType;
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
        // Ya no iniciamos el ticker aquí.
        // Tu clase principal (NexoMinions.java) llamará a tickAll() cada segundo de forma más optimizada.
    }

    // Método para crear un minion en el mundo (Llamado al colocar el ítem)
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

    // Este método es llamado cada 1 segundo desde NexoMinions.java
    public void tickAll(long currentTimeMillis) {
        for (ActiveMinion minion : minionsActivos.values()) {

            // Si la entidad desapareció (alguien la mató o borró), la quitamos de la memoria
            if (minion.getEntity().isDead() || !minion.getEntity().isValid()) {
                minionsActivos.remove(minion.getEntity().getUniqueId());
                continue;
            }

            // Hacemos que la abeja revise si ya es hora de minar y gire su holograma
            minion.tick(currentTimeMillis);
        }
    }
}