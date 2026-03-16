package me.nexo.minions.manager;

import dev.aurelium.auraskills.api.AuraSkillsApi;
import dev.aurelium.auraskills.api.user.SkillsUser;
import gg.auroramc.collections.api.AuroraCollectionsProvider;
import gg.auroramc.collections.collection.CollectionManager;
import me.nexo.minions.data.MinionKeys;
import me.nexo.minions.data.MinionTier;
import me.nexo.minions.data.MinionType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.UUID;

public class ActiveMinion {
    private final ItemDisplay entity;
    private final UUID ownerId;
    private final MinionType type;
    private int tier;
    private long nextActionTime;

    public ActiveMinion(ItemDisplay entity, UUID ownerId, MinionType type, int tier, long nextActionTime) {
        this.entity = entity;
        this.ownerId = ownerId;
        this.type = type;
        this.tier = tier;
        this.nextActionTime = nextActionTime;
    }

    public void tick(long currentTimeMillis) {
        if (currentTimeMillis >= nextActionTime) {
            realizarTrabajo();

            MinionTier stats = MinionTier.getStats(tier);
            nextActionTime = currentTimeMillis + (long) (stats.getDelaySeconds() * 1000L);
            entity.getPersistentDataContainer().set(MinionKeys.NEXT_ACTION, org.bukkit.persistence.PersistentDataType.LONG, nextActionTime);
        }
        animar();
    }

    private void realizarTrabajo() {
        Location loc = entity.getLocation();

        // Efectos de partículas y sonido al minar
        loc.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, loc.clone().add(0, 0.5, 0), 3);
        loc.getWorld().playSound(loc, Sound.ENTITY_BEE_POLLINATE, 0.2f, 1.5f);

        // Generamos el ítem que recolectó el minion
        ItemStack drop = new ItemStack(type.getTargetMaterial());
        loc.getWorld().dropItemNaturally(loc.clone().add(0, 0.5, 0), drop);

        Player owner = Bukkit.getPlayer(ownerId);
        if (owner != null && owner.isOnline()) {

            // 🌟 1. HOOK AURORA COLLECTIONS 🌟
            try {
                // Tu ruta validada y correcta:
                CollectionManager api = AuroraCollectionsProvider.getCollectionManager();

                // Si la API se cargó correctamente, incrementamos la colección
                if (api != null) {
                    api.incrementCollection(owner, drop);
                }
            } catch (Exception e) {
                // Si Aurora falla, la abeja seguirá funcionando y no crasheará el servidor
                System.out.println("Error con AuroraCollections en NexoMinions: " + e.getMessage());
            }

            // 🌟 2. HOOK AURA SKILLS 🌟
            SkillsUser user = AuraSkillsApi.get().getUser(ownerId);
            if (user != null) {
                user.addSkillXp(type.getAuraSkill(), 2.0);
            }
        }
    }

    private void animar() {
        // Rotación suave para que el holograma de Nexo parezca vivo
        Transformation trans = entity.getTransformation();
        float nuevoAngulo = (System.currentTimeMillis() % 4000) / 4000f * (float) Math.PI * 2;
        trans.getLeftRotation().set(new AxisAngle4f(nuevoAngulo, new Vector3f(0, 1, 0)));
        entity.setTransformation(trans);
    }

    public ItemDisplay getEntity() { return entity; }
}