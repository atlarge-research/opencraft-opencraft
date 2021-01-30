package science.atlarge.opencraft.opencraft.block.blocktype;

import java.util.ArrayList;
import java.util.Collection;
import science.atlarge.opencraft.opencraft.block.GlowBlock;
import science.atlarge.opencraft.opencraft.entity.GlowPlayer;
import science.atlarge.opencraft.opencraft.entity.monster.GlowSilverfish;
import org.bukkit.GameMode;
import org.bukkit.block.BlockFace;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;

public class BlockMonsterEgg extends BlockType {

    @Override
    public void blockDestroy(GlowPlayer player, GlowBlock block, BlockFace face) {
        //only spawn silverfish if block broke by player in survival
        if (player.getGameMode() == GameMode.SURVIVAL) {
            block.getWorld().spawn(block.getLocation().add(0.5, 0, 0.5), GlowSilverfish.class,
                CreatureSpawnEvent.SpawnReason.SILVERFISH_BLOCK);
        }
    }

    @Override
    public Collection<ItemStack> getDrops(GlowBlock block, ItemStack tool) {
        // monster egg blocks do not drop blocks
        return new ArrayList<>();
    }
}
