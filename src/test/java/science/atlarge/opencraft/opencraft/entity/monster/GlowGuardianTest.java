package science.atlarge.opencraft.opencraft.entity.monster;

import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.eq;

import java.util.UUID;
import java.util.function.Function;
import science.atlarge.opencraft.opencraft.io.entity.EntityStorage;
import science.atlarge.opencraft.opencraft.util.nbt.CompoundTag;
import org.bukkit.Location;
import org.bukkit.World;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class GlowGuardianTest extends GlowMonsterTest<GlowGuardian> {

    public GlowGuardianTest() {
        this(GlowGuardian::new);
    }

    protected GlowGuardianTest(
            Function<Location, ? extends GlowGuardian> entityCreator) {
        super(entityCreator);
    }

    @Test
    public void testIsElder() {
        assertFalse(entity.isElder());
    }

    @Test
    public void testSetElder() {
        UUID uuid = new UUID(100, 200);
        GlowElderGuardian other = new GlowElderGuardian(entity.getLocation());
        Mockito.when(world.spawn(eq(entity.getLocation()), eq(GlowElderGuardian.class))).thenReturn(other);
        Mockito.when(world.getUID()).thenReturn(uuid);
        Mockito.when(world.getEnvironment()).thenReturn(World.Environment.NORMAL);
        Mockito.when(server.getWorld(uuid)).thenReturn(world);

        entity.setFireTicks(23);
        Assert.assertEquals(23, entity.getFireTicks());
        CompoundTag tag = new CompoundTag();
        tag.putShort("Fire", 23);
        Assert.assertEquals(23, tag.getShort("Fire"));
        EntityStorage.save(entity, tag);

        entity.setElder(true);
        Assert.assertEquals(23, other.getFireTicks());
    }


}
