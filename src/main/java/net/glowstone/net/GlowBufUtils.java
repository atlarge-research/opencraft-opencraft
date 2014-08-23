package net.glowstone.net;

import com.flowpowered.networking.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.GlowServer;
import net.glowstone.entity.meta.MetadataIndex;
import net.glowstone.entity.meta.MetadataMap;
import net.glowstone.inventory.GlowItemFactory;
import net.glowstone.util.nbt.CompoundTag;
import net.glowstone.util.nbt.NBTInputStream;
import net.glowstone.util.nbt.NBTOutputStream;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;

/**
 * Contains several utility methods for writing special data types to @{link ByteBuf}s.
 */
public final class GlowBufUtils {

    private GlowBufUtils() {}

    /**
     * Writes a list of mob metadata entries to the buffer.
     * @param buf The buffer.
     * @param entries The metadata.
     */
    public static void writeMetadata(ByteBuf buf, List<MetadataMap.Entry> entries) throws IOException {
        for (MetadataMap.Entry entry : entries) {
            MetadataIndex index = entry.index;
            Object value = entry.value;

            if (value == null) continue;

            int type = index.getType().getId();
            int id = index.getIndex();
            buf.writeByte((type << 5) | id);

            switch (index.getType()) {
                case BYTE:
                    buf.writeByte((Byte) value);
                    break;
                case SHORT:
                    buf.writeShort((Short) value);
                    break;
                case INT:
                    buf.writeInt((Integer) value);
                    break;
                case FLOAT:
                    buf.writeFloat((Float) value);
                    break;
                case STRING:
                    ByteBufUtils.writeUTF8(buf, (String) value);
                    break;
                case ITEM:
                    writeSlot(buf, (ItemStack) value);
                    break;
            }
        }

        buf.writeByte(127);
    }

    /**
     * Read a length-prefixed, compressed compound NBT tag from the buffer.
     * @param buf The buffer.
     * @return The tag read, or null.
     */
    public static CompoundTag readCompound(ByteBuf buf) {
        int len = buf.readShort();
        if (len < 0) {
            return null;
        }

        byte[] bytes = new byte[len];
        buf.readBytes(bytes);

        try (NBTInputStream str = new NBTInputStream(new ByteArrayInputStream(bytes))) {
            return str.readCompound();
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Write a length-prefixed, compressed compound NBT tag to the buffer.
     * @param buf The buffer.
     * @param data The tag to write, or null.
     */
    public static void writeCompound(ByteBuf buf, CompoundTag data) {
        if (data == null) {
            buf.writeShort(-1);
            return;
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (NBTOutputStream str = new NBTOutputStream(out)) {
            str.writeTag(data);
        } catch (IOException e) {
            GlowServer.logger.log(Level.WARNING, "Error serializing NBT: " + data, e);
            return;
        }

        buf.writeShort(out.size());
        buf.writeBytes(out.toByteArray());
    }

    /**
     * Write an item stack to the buffer.
     * @param buf The buffer.
     * @param stack The stack to write, or null.
     */
    public static void writeSlot(ByteBuf buf, ItemStack stack) {
        if (stack == null || stack.getTypeId() == 0) {
            buf.writeShort(-1);
        } else {
            buf.writeShort(stack.getTypeId());
            buf.writeByte(stack.getAmount());
            buf.writeShort(stack.getDurability());

            if (stack.hasItemMeta()) {
                //GlowServer.logger.info("Writing item meta: " + stack.getItemMeta());
                CompoundTag tag = GlowItemFactory.instance().writeNbt(stack.getItemMeta());
                writeCompound(buf, tag);
                //GlowServer.logger.info("Wrote item tag: " + tag);
            } else {
                writeCompound(buf, null);
            }
        }
    }

    /**
     * Read an item stack from the buffer.
     * @param buf The buffer.
     * @return The stack read, or null.
     */
    public static ItemStack readSlot(ByteBuf buf) {
        short type = buf.readShort();
        if (type == -1) {
            return null;
        }

        int amount = buf.readUnsignedByte();
        short durability = buf.readShort();

        Material material = Material.getMaterial(type);
        if (material == null) {
            return null;
        }

        CompoundTag tag = readCompound(buf);
        //GlowServer.logger.info("Reading item tag: " + tag);
        ItemStack stack = new ItemStack(material, amount, durability);
        stack.setItemMeta(GlowItemFactory.instance().readNbt(material, tag));
        //GlowServer.logger.info("Read item meta: " + stack.getItemMeta());
        return stack;
    }

    /**
     * Read an encoded block vector (position) from the buffer.
     * @param buf The buffer.
     * @return The vector read.
     */
    public static BlockVector readBlockPosition(ByteBuf buf) {
        long val = buf.readLong();
        long x = (val >> 38) & 0x3ffffff;
        long y = (val >> 26) & 0xfff;
        long z = val & 0x3ffffff;
        return new BlockVector((double) x, y, z);
    }

    /**
     * Write an encoded block vector (position) to the buffer.
     * @param buf The buffer.
     * @param vector The vector to write.
     */
    public static void writeBlockPosition(ByteBuf buf, Vector vector) {
        writeBlockPosition(buf, vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
    }

    /**
     * Write an encoded block vector (position) to the buffer.
     * @param buf The buffer.
     * @param x The x value.
     * @param y The y value.
     * @param z The z value.
     */
    public static void writeBlockPosition(ByteBuf buf, long x, long y, long z) {
        buf.writeLong(((x & 0x3ffffff) << 38) | ((y & 0xfff) << 26) | (z & 0x3ffffff));
    }

}
