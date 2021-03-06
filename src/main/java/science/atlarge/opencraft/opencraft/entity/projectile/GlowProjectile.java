package science.atlarge.opencraft.opencraft.entity.projectile;

import com.flowpowered.network.Message;
import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import science.atlarge.opencraft.opencraft.EventFactory;
import science.atlarge.opencraft.opencraft.entity.GlowEntity;
import science.atlarge.opencraft.opencraft.net.message.play.entity.EntityMetadataMessage;
import science.atlarge.opencraft.opencraft.net.message.play.entity.EntityVelocityMessage;
import science.atlarge.opencraft.opencraft.net.message.play.entity.SpawnObjectMessage;
import science.atlarge.opencraft.opencraft.util.Position;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

/**
 * A projectile. Subclasses must call {@link #setBoundingBox(double, double)} if they are to collide
 * with other entities.
 */
public abstract class GlowProjectile extends GlowEntity implements Projectile {

    @Getter
    @Setter
    private boolean glowing;
    @Getter
    @Setter
    private boolean invulnerable;
    @Getter
    @Setter
    private ProjectileSource shooter;
    @Setter
    private boolean bounce;

    /**
     * Creates a projectile.
     *
     * @param location the initial location
     */
    public GlowProjectile(Location location) {
        super(location);
    }

    @Override
    public List<Message> createSpawnMessage() {
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();

        int yaw = Position.getIntYaw(location);
        int pitch = Position.getIntPitch(location);

        return Arrays.asList(
                new SpawnObjectMessage(entityId, getUniqueId(), getObjectId(), x, y, z, pitch, yaw),
                new EntityMetadataMessage(entityId, metadata.getEntryList()),
                new EntityVelocityMessage(entityId, getVelocity())
        );
    }

    @Override
    protected void pulsePhysics() {
        if (boundingBox != null) {
            Vector size = boundingBox.getSize();
            for (Entity entity : world.getNearbyEntities(
                    location, size.getX(), size.getY(), size.getZ())) {
                if (entity != this && !(entity.equals(shooter))) {
                    if (entity instanceof LivingEntity) {
                        EventFactory.getInstance().callEvent(new ProjectileHitEvent(this, entity));
                        collide((LivingEntity) entity);
                        break;
                    }
                }
            }
        }
        super.pulsePhysics();
    }

    @Override
    protected boolean hasDefaultLandingBehavior() {
        return false;
    }

    @Override
    public abstract void collide(Block block);

    public abstract void collide(LivingEntity entity);

    protected abstract int getObjectId();

    @Override
    public boolean doesBounce() {
        return bounce;
    }
}