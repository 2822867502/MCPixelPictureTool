package org.zlk.mcpixelpicturetool.type;

import net.querz.nbt.tag.CompoundTag;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class BlocksToPalettesMap {
    private final LinkedHashMap<Block, CompoundTag> blockToCompoundTag = new LinkedHashMap<>();
    private final HashMap<Block, Integer> blockToStateIndex = new HashMap<>();

    public void put(Block block) {
        if (!blockToCompoundTag.containsKey(block)) {
            blockToStateIndex.put(block, blockToCompoundTag.size());
        }
        blockToCompoundTag.put(block, block.toCompoundTag());
    }

    public int getBlockState(Block block) {
        return blockToStateIndex.get(block);
    }

    public Collection<CompoundTag> getPalettes() {
        return blockToCompoundTag.values();
    }
}
