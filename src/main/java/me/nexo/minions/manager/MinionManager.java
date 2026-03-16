package me.nexo.minions.manager;

import com.nexomc.nexo.api.NexoItems;
import me.nexo.minions.NexoMinions;
import me.nexo.minions.data.MinionKeys;
import me.nexo.minions.data.MinionType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MinionManager {

    private final NexoMinions plugin;
    public final ConcurrentHashMap<UUID, ActiveMinion> minionsActivos = new ConcurrentHashMap<>();

    public MinionManager(NexoMinions plugin) {
        this.plugin = plugin;
        MinionKeys.init(plugin);
        iniciarTicker();
    }

    // ==========================================
    // 🐝 INVOCACIÓN DEL MINION
    // ==========================================
    public void spawnMinion(Location loc, UUID ownerId, MinionType type, int tier) {
        loc.getWorld().spawn(loc, ItemDisplay.class, display -> {
            // Asignamos el modelo 3D desde Nexo
            var nexoItemBuilder = NexoItems.itemFromId(type.getNexoModelID());
            if (nexoItemBuilder != null) {
                display.setItemStack(nexoItemBuilder.build());
            }

            // Configuraciones visuales modernas (1.21+)
            display.setBillboard(ItemDisplay.Billboard.FIXED);
            display.setInvulnerable(true);

            // Guardamos la Metadata en el PDC
            var pdc = display.getPersistentDataContainer();
            pdc.set(MinionKeys.OWNER, PersistentDataType.STRING, ownerId.toString());
            pdc.set(MinionKeys.TYPE, PersistentDataType.STRING, type.name());
            pdc.set(MinionKeys.TIER, PersistentDataType.INTEGER, tier);
            pdc.set(MinionKeys.NEXT_ACTION, PersistentDataType.LONG, System.currentTimeMillis());

            // Lo registramos en la RAM
            ActiveMinion minion = new ActiveMinion(display, ownerId, type, tier, System.currentTimeMillis());
            minionsActivos.put(display.getUniqueId(), minion);
        });
    }

    // ==========================================
    // ⚙️ EL MOTOR DE TICK (Anti-Lag)
    // ==========================================
    private void iniciarTicker() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            long ahora = System.currentTimeMillis();

            for (ActiveMinion minion : minionsActivos.values()) {
                // Verificamos si la entidad fue borrada o el chunk se descargó
                if (minion.getEntity().isDead() || !minion.getEntity().isValid()) {
                    minionsActivos.remove(minion.getEntity().getUniqueId());
                    continue;
                }

                // Ejecutamos su lógica (Farmeo y Animación)
                minion.tick(ahora);
            }
        }, 1L, 1L); // Se ejecuta cada tick (20 veces por segundo) para animaciones suaves
    }

    // Este método se llamaría al cargar chunks para revivir a los minions que ya estaban puestos
    public void cargarMinionDesdeEntidad(ItemDisplay display) {
        var pdc = display.getPersistentDataContainer();
        if (pdc.has(MinionKeys.TYPE, PersistentDataType.STRING)) {
            UUID ownerId = UUID.fromString(pdc.get(MinionKeys.OWNER, PersistentDataType.STRING));
            MinionType type = MinionType.valueOf(pdc.get(MinionKeys.TYPE, PersistentDataType.STRING));
            int tier = pdc.getOrDefault(MinionKeys.TIER, PersistentDataType.INTEGER, 1);
            long nextAction = pdc.getOrDefault(MinionKeys.NEXT_ACTION, PersistentDataType.LONG, System.currentTimeMillis());

            ActiveMinion minion = new ActiveMinion(display, ownerId, type, tier, nextAction);
            minionsActivos.put(display.getUniqueId(), minion);
        }
    }
}