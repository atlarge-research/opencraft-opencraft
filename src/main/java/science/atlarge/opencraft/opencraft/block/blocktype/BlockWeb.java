package science.atlarge.opencraft.opencraft.block.blocktype;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import science.atlarge.opencraft.opencraft.block.GlowBlock;
import science.atlarge.opencraft.opencraft.inventory.ToolType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class BlockWeb extends BlockType {

    private static final Collection<ItemStack> DROP_STRING = Arrays
        .asList(new ItemStack(Material.STRING));

    @Override
    public Collection<ItemStack> getDrops(GlowBlock block, ItemStack tool) {
        if (tool != null && (tool.getType() == Material.SHEARS || ToolType.SWORD
            .matches(tool.getType()))) {
            return DROP_STRING;
        }
        return Collections.emptyList();
    }

    @Override
    public Collection<ItemStack> getMinedDrops(GlowBlock block) {
        return DROP_STRING;
    }
}
