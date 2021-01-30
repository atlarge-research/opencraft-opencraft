package science.atlarge.opencraft.opencraft.net.message.play.entity;

import com.flowpowered.network.Message;
import java.util.Map;
import lombok.Data;
import science.atlarge.opencraft.opencraft.entity.AttributeManager.Property;

@Data
public final class EntityPropertyMessage implements Message {

    private final int id;
    private final Map<String, Property> properties;

}
