package science.atlarge.opencraft.opencraft.entity.projectile;

import lombok.Getter;
import lombok.Setter;
import science.atlarge.opencraft.opencraft.net.message.play.entity.SpawnObjectMessage;
import org.bukkit.Location;
import org.bukkit.entity.WitherSkull;

public class GlowWitherSkull extends GlowFireball implements WitherSkull {
    @Getter
    @Setter
    private boolean charged;

    public GlowWitherSkull(Location location) {
        super(location);
    }

    @Override
    protected int getObjectId() {
        return SpawnObjectMessage.WITHER_SKULL;
    }
}
