package net.glowstone.entity.passive;

import net.glowstone.entity.meta.MetadataIndex;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Ocelot;

public class GlowOcelot extends GlowTameable implements Ocelot {

    private Type catType;

    public GlowOcelot(Location location) {
        super(location, EntityType.OCELOT, 10);
        setCatType(Type.WILD_OCELOT);
    }

    @Override
    public Type getCatType() {
        return catType;
    }

    @Override
    public void setCatType(Type type) {
        metadata.set(MetadataIndex.OCELOT_TYPE, catType.getId());
        this.catType = type;
    }

}
