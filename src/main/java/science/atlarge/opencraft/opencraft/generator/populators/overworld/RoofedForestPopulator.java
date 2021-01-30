package science.atlarge.opencraft.opencraft.generator.populators.overworld;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import science.atlarge.opencraft.opencraft.generator.decorators.overworld.TreeDecorator.TreeDecoration;
import science.atlarge.opencraft.opencraft.generator.objects.trees.BirchTree;
import science.atlarge.opencraft.opencraft.generator.objects.trees.BrownMushroomTree;
import science.atlarge.opencraft.opencraft.generator.objects.trees.DarkOakTree;
import science.atlarge.opencraft.opencraft.generator.objects.trees.GenericTree;
import science.atlarge.opencraft.opencraft.generator.objects.trees.RedMushroomTree;
import org.bukkit.block.Biome;

public class RoofedForestPopulator extends ForestPopulator {

    private static final Biome[] BIOMES = {Biome.ROOFED_FOREST, Biome.MUTATED_ROOFED_FOREST};
    private static final TreeDecoration[] TREES = {new TreeDecoration(GenericTree::new, 20),
        new TreeDecoration(BirchTree::new, 5),
        new TreeDecoration(RedMushroomTree::new, 2),
        new TreeDecoration(BrownMushroomTree::new, 2), new TreeDecoration(DarkOakTree::new, 50)};

    /**
     * Creates a populator specialized for the Roofed Forest and Roofed Forest M biomes.
     */
    public RoofedForestPopulator() {
        treeDecorator.setAmount(50);
        treeDecorator.setTrees(TREES);
        tallGrassDecorator.setAmount(4);
    }

    @Override
    public Collection<Biome> getBiomes() {
        return Collections.unmodifiableList(Arrays.asList(BIOMES));
    }
}
