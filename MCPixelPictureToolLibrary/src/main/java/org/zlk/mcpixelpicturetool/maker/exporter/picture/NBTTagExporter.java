package org.zlk.mcpixelpicturetool.maker.exporter.picture;

import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.IntTag;
import net.querz.nbt.tag.ListTag;
import org.zlk.mcpixelpicturetool.maker.base.abstracts.AbstractExporter;
import org.zlk.mcpixelpicturetool.maker.base.interfaces.Generator;
import org.zlk.mcpixelpicturetool.type.BlocksToPalettesMap;
import org.zlk.mcpixelpicturetool.type.Point;
import org.zlk.mcpixelpicturetool.type.NBTTagOriginData;
import org.zlk.mcpixelpicturetool.utils.NBTUtils;

import java.io.IOException;
import java.util.HashSet;

public class NBTTagExporter extends AbstractExporter<NBTTagOriginData, CompoundTag> {
    private final BlocksToPalettesMap blocksToPalettesMap = new BlocksToPalettesMap();
    private final HashSet<CompoundTag> blocks = new HashSet<>();
    public NBTTagExporter(String name, Generator generator) {
        super(name,generator);
    }

    @Override
    public void export() {
        ListTag<IntTag> size = NBTUtils.getSize(input.size[0], input.size[1], input.size[2]);
        IntTag version = NBTUtils.getVersion(input.dataVersion);
        for (Point point : input.blocks.keySet()) {
            int[] pointIntArray = point.getPoint();
            blocksToPalettesMap.put(input.blocks.get(point));
            blocks.add(NBTUtils.getBlockNBT(blocksToPalettesMap, input.blocks.get(point), pointIntArray[0], pointIntArray[1], pointIntArray[2]));
        }
        ListTag<CompoundTag> blockTags = NBTUtils.getBlocks(blocks);
        ListTag<CompoundTag> palettes = NBTUtils.getPalettesMap(blocksToPalettesMap);
        output = NBTUtils.getRoot(size,version,blockTags,palettes);
    }

    @Override
    public void clear() {
        super.clear();
        this.input = null;
    }
}
