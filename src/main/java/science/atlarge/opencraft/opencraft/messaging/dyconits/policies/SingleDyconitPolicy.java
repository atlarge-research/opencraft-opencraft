package science.atlarge.opencraft.opencraft.messaging.dyconits.policies;

import com.flowpowered.network.Message;
import java.util.Collections;
import java.util.List;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import science.atlarge.opencraft.dyconits.Bounds;
import science.atlarge.opencraft.dyconits.Subscriber;
import science.atlarge.opencraft.dyconits.policies.DyconitCommand;
import science.atlarge.opencraft.dyconits.policies.DyconitPolicy;
import science.atlarge.opencraft.dyconits.policies.DyconitSubscribeCommand;

public class SingleDyconitPolicy implements DyconitPolicy<Player, Message> {

    private final int staleness;
    private final int numerical;
    private final static String DYCONIT_NAME = "single";

    public SingleDyconitPolicy(int staleness, int numerical) {
        this.staleness = staleness;
        this.numerical = numerical;
    }

    @NotNull
    @Override
    public String computeAffectedDyconit(@NotNull Object o) {
        return DYCONIT_NAME;
    }

    @NotNull
    @Override
    public List<DyconitCommand<Player, Message>> update(@NotNull Subscriber<Player, Message> subscriber) {
        return Collections.singletonList(new DyconitSubscribeCommand<>(subscriber.getKey(), subscriber.getCallback(), new Bounds(staleness, numerical), DYCONIT_NAME));
    }

    @Override
    public int weigh(Message message) {
        return 1;
    }
}