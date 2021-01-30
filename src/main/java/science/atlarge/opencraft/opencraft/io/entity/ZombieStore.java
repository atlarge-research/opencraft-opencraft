package science.atlarge.opencraft.opencraft.io.entity;

import java.util.function.Function;
import science.atlarge.opencraft.opencraft.entity.monster.GlowZombie;
import science.atlarge.opencraft.opencraft.util.nbt.CompoundTag;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

class ZombieStore<T extends GlowZombie> extends MonsterStore<GlowZombie> {

    public ZombieStore() {
        super(GlowZombie.class, EntityType.ZOMBIE, GlowZombie::new);
    }

    public ZombieStore(Class<T> clazz, EntityType type, Function<Location, T> creator) {
        super(clazz, type, creator);
    }

    @Override
    public void load(GlowZombie entity, CompoundTag tag) {
        super.load(entity, tag);
        entity.setBaby(tag.getBoolean("IsBaby", false));
        entity.setCanBreakDoors(tag.getBoolean("CanBreakDoors", true));
    }

    @Override
    public void save(GlowZombie entity, CompoundTag tag) {
        super.save(entity, tag);
        tag.putBool("IsBaby", entity.isBaby());
        tag.putBool("CanBreakDoors", entity.isCanBreakDoors());
    }
}
