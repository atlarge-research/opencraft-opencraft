package science.atlarge.opencraft.opencraft.command.opencraft;

import com.flowpowered.network.Message;
import java.util.ResourceBundle;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import science.atlarge.opencraft.dyconits.policies.DyconitPolicy;
import science.atlarge.opencraft.opencraft.command.minecraft.GlowVanillaCommand;
import science.atlarge.opencraft.opencraft.entity.GlowPlayer;
import science.atlarge.opencraft.opencraft.i18n.LocalizedStringImpl;
import science.atlarge.opencraft.opencraft.messaging.DyconitMessaging;
import science.atlarge.opencraft.opencraft.messaging.Messaging;
import science.atlarge.opencraft.opencraft.messaging.dyconits.policies.PolicyFactory;

public class DyconitPolicyCommand extends GlowVanillaCommand {

    public DyconitPolicyCommand() {
        // TODO give command a good name
        super("dcpolicy");
    }

    @Override
    protected boolean execute(CommandSender sender, String commandLabel, String[] args, CommandMessages localizedMessages) {
        final ResourceBundle resourceBundle = localizedMessages.getResourceBundle();
        GlowPlayer player = (GlowPlayer) sender;
        Messaging messaging = player.getWorld().getMessagingSystem();

        if (messaging instanceof DyconitMessaging) {
            DyconitMessaging dm = (DyconitMessaging) messaging;
            if (args.length > 0) {
                DyconitPolicy<Player, Message> policy = PolicyFactory.policyFromString(args[0], player.getServer());
                if (policy == null) {
                    return false;
                }
                dm.setPolicy(policy);
            }
            new LocalizedStringImpl("dcpolicy.done", resourceBundle).send(sender, dm.getPolicy().getClass().getSimpleName());
            return true;
        } else {
            // TODO, ERROR
            return false;
        }
    }
}
