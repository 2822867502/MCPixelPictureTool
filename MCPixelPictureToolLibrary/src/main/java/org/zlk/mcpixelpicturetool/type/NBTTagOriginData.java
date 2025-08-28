package org.zlk.mcpixelpicturetool.type;

import org.zlk.mcpixelpicturetool.type.Block;
import org.zlk.mcpixelpicturetool.type.BlocksToPalettesMap;
import org.zlk.mcpixelpicturetool.type.container.PointsContainer;

import java.util.HashSet;

public class NBTTagOriginData {
    public static final int DEFAULT_DATA_VERSION = 0;
    public final int[] size;
    public final int dataVersion;
    public final PointsContainer<Block> blocks;

    public NBTTagOriginData(int[] size, int dataVersion, PointsContainer<Block> blocks) {
        this.size = size;
        this.dataVersion = dataVersion;
        this.blocks = blocks;
    }
}
