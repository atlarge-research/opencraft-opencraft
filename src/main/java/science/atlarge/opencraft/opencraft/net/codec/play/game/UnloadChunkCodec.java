package science.atlarge.opencraft.opencraft.net.codec.play.game;

import com.flowpowered.network.Codec;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import science.atlarge.opencraft.opencraft.net.message.play.game.UnloadChunkMessage;

public class UnloadChunkCodec implements Codec<UnloadChunkMessage> {

    @Override
    public UnloadChunkMessage decode(ByteBuf buffer) throws IOException {
        int chunkX = buffer.readInt();
        int chunkZ = buffer.readInt();
        return new UnloadChunkMessage(chunkX, chunkZ);
    }

    @Override
    public ByteBuf encode(ByteBuf buffer, UnloadChunkMessage message) throws IOException {
        buffer.writeInt(message.getChunkX());
        buffer.writeInt(message.getChunkZ());
        return buffer;
    }
}
