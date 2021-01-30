package science.atlarge.opencraft.opencraft.io.entity;

import science.atlarge.opencraft.opencraft.entity.passive.GlowBat;
import science.atlarge.opencraft.opencraft.util.nbt.CompoundTag;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

class BatStore extends LivingEntityStore<GlowBat> {

    public BatStore() {
        super(GlowBat.class, EntityType.BAT);
    }

    @Override
    public GlowBat createEntity(Location location, CompoundTag compound) {
        return new GlowBat(location);
    }

    @Override
    public void load(GlowBat entity, CompoundTag compound) {
        super.load(entity, compound);
        entity.setAwake(compound.getBoolean("BatFlags", true));
    }

    @Override
    public void save(GlowBat entity, CompoundTag tag) {
        super.save(entity, tag);
        tag.putBool("BatFlags", entity.isAwake());
    }
}
