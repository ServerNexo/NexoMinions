package me.nexo.minions.manager;

import com.nexomc.nexo.api.NexoItems;
import me.nexo.minions.data.MinionKeys;
import me.nexo.minions.data.MinionTier;
import me.nexo.minions.data.MinionType;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.ItemDisplay;
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

            // Calculamos el próximo tick basado en su Tier
            MinionTier stats = MinionTier.getStandardTier(tier, "enchanted_" + type.name().toLowerCase());
            long delayMillis = (long) (stats.getDelaySeconds() * 1000L);
            nextActionTime = currentTimeMillis + delayMillis;

            // Actualizamos en la entidad por si se reinicia el server (Failsafe)
            entity.getPersistentDataContainer().set(MinionKeys.NEXT_ACTION, org.bukkit.persistence.PersistentDataType.LONG, nextActionTime);
        }

        // Animación de flotación suave (girando poco a poco)
        animar();
    }

    private void realizarTrabajo() {
        Location loc = entity.getLocation();

        // Efectos visuales de que está trabajando
        loc.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, loc.clone().add(0, 0.5, 0), 3);
        loc.getWorld().playSound(loc, Sound.ENTITY_BEE_POLLINATE, 0.2f, 1.5f);

        // AQUÍ VA LA FASE 3: Generación de ítem, Hook a AuraSkills y AuroraCollections
        // loc.getWorld().dropItem(loc, new ItemStack(type.getTargetMaterial()));
    }

    private void animar() {
        // Hacemos que el minion mire lentamente hacia los lados simulando vida
        Transformation trans = entity.getTransformation();
        float nuevoAngulo = (System.currentTimeMillis() % 4000) / 4000f * (float) Math.PI * 2;
        trans.getLeftRotation().set(new AxisAngle4f(nuevoAngulo, new Vector3f(0, 1, 0)));
        entity.setTransformation(trans);
    }

    public ItemDisplay getEntity() { return entity; }
    public UUID getOwnerId() { return ownerId; }
    public MinionType getType() { return type; }
}
    private void realizarTrabajo() {
        Location loc = entity.getLocation();

        // Efectos visuales de que está trabajando
        loc.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, loc.clone().add(0, 0.5, 0), 3);
        loc.getWorld().playSound(loc, Sound.ENTITY_BEE_POLLINATE, 0.2f, 1.5f);

        // AQUÍ VA LA FASE 3: Generación de ítem, Hook a AuraSkills y AuroraCollections
        // loc.getWorld().dropItem(loc, new ItemStack(type.getTargetMaterial()));
    }

    private void animar() {
        // Hacemos que el minion mire lentamente hacia los lados simulando vida
        Transformation trans = entity.getTransformation();
        float nuevoAngulo = (System.currentTimeMillis() % 4000) / 4000f * (float) Math.PI * 2;
        trans.getLeftRotation().set(new AxisAngle4f(nuevoAngulo, new Vector3f(0, 1, 0)));
        entity.setTransformation(trans);
    }

    public ItemDisplay getEntity() { return entity; }
    public UUID getOwnerId() { return ownerId; }
    public MinionType getType() { return type; }
}