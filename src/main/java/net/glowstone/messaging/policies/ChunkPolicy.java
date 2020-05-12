package net.glowstone.messaging.policies;

import java.util.HashSet;
import java.util.Set;
import net.glowstone.messaging.Policy;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

/**
 * Defines a mapping from players to chunks of interest and from objects to the chunk in which they reside.
 */
public final class ChunkPolicy implements Policy<Chunk, Player, Object> {

    private final World world;
    private final int viewDistance;

    /**
     * Create a chunk policy for the given world and view distance.
     *
     * @param world the world from which chunks may be interesting.
     * @param viewDistance the maximum view distance of any player.
     */
    public ChunkPolicy(World world, int viewDistance) {
        this.world = world;
        this.viewDistance = viewDistance;
    }

    @Override
    public Set<Chunk> computeInterestSet(Player player) {

        Location location = player.getLocation();
        if (location.getWorld() != world) {
            return new HashSet<>();
        }

        int centerX = location.getBlockX() >> 4;
        int centerZ = location.getBlockZ() >> 4;
        int radius = Math.min(viewDistance, player.getViewDistance());

        Set<Chunk> chunks = new HashSet<>();
        for (int x = centerX - radius; x <= centerX + radius; x++) {
            for (int z = centerZ - radius; z <= centerZ + radius; z++) {
                Chunk chunk = world.getChunkAt(x, z);
                chunks.add(chunk);
            }
        }
        return chunks;
    }

    @Override
    public Chunk selectTarget(Object publisher) {

        if (publisher instanceof Chunk) {
            return (Chunk) publisher;
        }

        if (publisher instanceof Block) {
            return ((Block) publisher).getChunk();
        }

        if (publisher instanceof Entity) {
            return ((Entity) publisher).getChunk();
        }

        throw new UnsupportedOperationException("Cannot select target topic for type: " + publisher.getClass());
    }
}
