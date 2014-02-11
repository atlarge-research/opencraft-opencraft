package net.glowstone.io.nbt;

import net.glowstone.util.nbt.*;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.*;

public class NbtSerialization {

    public static ItemStack[] tagToInventory(ListTag<CompoundTag> tagList, int size) {
       ItemStack[] items = new ItemStack[size];
        for (CompoundTag tag: tagList.getValue()) {
            Map<String, Tag> tagItems = tag.getValue();
            Tag idTag = tagItems.get("id");
            Tag damageTag = tagItems.get("Damage");
            Tag countTag = tagItems.get("Count");
            Tag slotTag = tagItems.get("Slot");
            short id = (idTag == null) ? 0 : ((ShortTag)idTag).getValue();
            short damage = (damageTag == null) ? 0 : ((ShortTag)damageTag).getValue();
            byte count = (countTag == null) ? 0 : ((ByteTag)countTag).getValue();
            byte slot = (slotTag == null) ? -1 : ((ByteTag)slotTag).getValue();
            if (id != 0 && slot >= 0 && count != 0) {
                if (items.length > slot) {
                    items[slot] = new ItemStack(id, count, damage);
                }
            }
        }
        return items;
    }

    public static ListTag<CompoundTag> inventoryToTag(ItemStack[] items) {
        List<CompoundTag> out = new ArrayList<CompoundTag>();
        for (int i = 0; i < items.length; i++) {
            ItemStack stack = items[i];
            if (stack != null) {
                List<Tag> nbtItem = new ArrayList<Tag>(4);
                nbtItem.add(new ShortTag("id", (short) stack.getTypeId()));
                nbtItem.add(new ShortTag("Damage", stack.getDurability()));
                nbtItem.add(new ByteTag("Count", (byte) stack.getAmount()));
                nbtItem.add(new ByteTag("Slot", (byte) i));
                out.add(new CompoundTag("", nbtItem));
            }
        }
        return new ListTag<CompoundTag>("Inventory", TagType.COMPOUND, out);
    }

    public static Location listTagsToLocation(World world, ListTag<DoubleTag> pos, ListTag<FloatTag> rot) {
        List<DoubleTag> posList = pos.getValue();
        List<FloatTag> rotList = rot.getValue();
        if (posList.size() == 3 && rotList.size() == 2) {
            return new Location(world, posList.get(0).getValue(), posList.get(1).getValue(), posList.get(2).getValue(), rotList.get(0).getValue(), rotList.get(1).getValue());
        }
        return world.getSpawnLocation();
    }

    public static Map<String, Tag> locationToListTags(Location loc) {
        List<DoubleTag> posList = new ArrayList<DoubleTag>();
        List<FloatTag> rotList = new ArrayList<FloatTag>();
        Map<String, Tag> ret = new HashMap<String, Tag>();
        posList.add(new DoubleTag("", loc.getX()));
        posList.add(new DoubleTag("", loc.getY()));
        posList.add(new DoubleTag("", loc.getZ()));
        ret.put("Pos", new ListTag<DoubleTag>("Pos", TagType.DOUBLE, posList));
        rotList.add(new FloatTag("", loc.getYaw()));
        rotList.add(new FloatTag("", loc.getPitch()));
        ret.put("Rotation", new ListTag<FloatTag>("Rotation", TagType.FLOAT, rotList));
        return ret;
    }

    public static Vector listTagToVector(ListTag<DoubleTag> tag) {
        List<DoubleTag> vecList = tag.getValue();
        if (vecList.size() == 3) {
            return new Vector(vecList.get(0).getValue(), vecList.get(1).getValue(), vecList.get(2).getValue());
        }
        return new Vector(0, 0, 0);
    }
    public static ListTag<DoubleTag> vectorToListTag(Vector vec) {
        List<DoubleTag> ret = new ArrayList<DoubleTag>();
        ret.add(new DoubleTag("", vec.getX()));
        ret.add(new DoubleTag("", vec.getY()));
        ret.add(new DoubleTag("", vec.getZ()));
        return new ListTag<DoubleTag>("Motion", TagType.DOUBLE, ret);
    }

}
