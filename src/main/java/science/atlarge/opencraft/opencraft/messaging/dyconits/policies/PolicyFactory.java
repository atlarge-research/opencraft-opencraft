package science.atlarge.opencraft.opencraft.messaging.dyconits.policies;

import com.flowpowered.network.Message;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import science.atlarge.opencraft.dyconits.policies.DyconitPolicy;

public class PolicyFactory {
    public static @Nullable DyconitPolicy<Player, Message> policyFromString(String policyName, Server server) {
        if (nameMatches(ChunkPolicy.class, policyName)) {
            return new ChunkPolicy(server.getViewDistance());
        } else if (nameMatches(ZeroBoundsPolicy.class, policyName)) {
            return new ZeroBoundsPolicy();
        } else if (nameMatches(InfiniteBoundsPolicy.class, policyName)) {
            return new InfiniteBoundsPolicy();
        } else if (nameMatches(DonnybrookPolicy.class, policyName)) {
            return new DonnybrookPolicy();
        } else if (nameMatches(SingleDyconitPolicy.class, policyName.split(";")[0])) {
            // TODO support optional options in opencraft.yml that can hold these parameters.
            String[] parts = policyName.split(";");
            return new SingleDyconitPolicy(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
        } else if (nameMatches(XDyconitsPolicy.class, policyName.split(";")[0])) {
            String[] parts = policyName.split(";");
            return new XDyconitsPolicy(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));
        } else if (nameMatches(NumericalDonnybrookPolicy.class, policyName)) {
            return new NumericalDonnybrookPolicy();
        } else if (nameMatches(QuadraticGradientStalenessAoI.class, policyName)) {
            return new QuadraticGradientStalenessAoI(server.getViewDistance());
        }
        return null;
    }

    private static boolean nameMatches(Class<? extends DyconitPolicy<Player, Message>> policy, String possibleName) {
        return policy.getSimpleName().toLowerCase().startsWith(possibleName.toLowerCase());
    }
}
