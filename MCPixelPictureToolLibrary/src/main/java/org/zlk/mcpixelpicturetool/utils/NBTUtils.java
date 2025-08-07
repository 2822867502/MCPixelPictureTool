package org.zlk.mcpixelpicturetool.utils;

import net.querz.nbt.io.NBTUtil;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.IntTag;
import net.querz.nbt.tag.ListTag;
import org.zlk.mcpixelpicturetool.type.Block;
import org.zlk.mcpixelpicturetool.type.BlocksToPalettesMap;

import java.io.File;
import java.io.IOException;
import java.util.Set;

public class NBTUtils {
    public static ListTag<CompoundTag> getPalettesMap(BlocksToPalettesMap blocksToPalettes) {
        ListTag<CompoundTag> result = new ListTag<>(CompoundTag.class);
        result.addAll(blocksToPalettes.getPalettes());
        return result;
    }

    public static CompoundTag getBlockNBT(BlocksToPalettesMap blocksToPalettes, Block block, int x, int y, int z) {
        CompoundTag result = new CompoundTag();
        ListTag<IntTag> pos = new ListTag<>(IntTag.class);
        pos.add(new IntTag(x));
        pos.add(new IntTag(y));
        pos.add(new IntTag(z));
        result.put("pos", pos);
        result.put("state", new IntTag(blocksToPalettes.getBlockState(block)));
        return result;
    }

    public static ListTag<CompoundTag> getBlocks(Set<CompoundTag> blocks) {
        ListTag<CompoundTag> result = new ListTag<>(CompoundTag.class);
        result.addAll(blocks);
        return result;
    }

    public static ListTag<IntTag> getSize(int x, int y, int z) {
        ListTag<IntTag> result = new ListTag<>(IntTag.class);
        result.add(new IntTag(x));
        result.add(new IntTag(y));
        result.add(new IntTag(z));
        return result;
    }

    public static IntTag getVersion(int version) {
        return new IntTag(version);
    }

    public static CompoundTag getRoot(ListTag<IntTag> size, IntTag version, ListTag<CompoundTag> blocks, ListTag<CompoundTag> palettes) {
        CompoundTag result = new CompoundTag();
        result.put("size", size);
        result.put("DataVersion", version);
        result.put("blocks", blocks);
        result.put("palette", palettes);
        return result;
    }

    public static void write(CompoundTag tag, File file) throws IOException {
        NBTUtil.write(tag, file, true);
    }
}
