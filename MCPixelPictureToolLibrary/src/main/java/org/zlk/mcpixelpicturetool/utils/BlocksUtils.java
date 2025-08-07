package org.zlk.mcpixelpicturetool.utils;

import org.zlk.mcpixelpicturetool.type.Block;

import java.util.Arrays;

public class BlocksUtils {
    public static Block parseBlock(String string) {
        if (string.isEmpty()) return null;
        String[] bs = string.split("=");
        if (bs.length != 2) return null;
        return new Block(bs[0], ColorUtils.hexToRGB(bs[1]));
    }

    public static Block[] parseBlocks(String string) throws Exception {
        try {
            //你可能会好奇为什么不用property包的东西简化，事实上，这个比property包写的早
            //todo 复用
            String blockString = string.replace("[", "").replace("]", "").replace(" ", "");
            if (blockString.isEmpty()) return new Block[0];
            String[] blockStrings = blockString.split(",");
            Block[] result = new Block[blockStrings.length];
            for (int i = 0; i < blockStrings.length; i++) {
                Block block = parseBlock(blockStrings[i]);
                if (block == null) continue;
                result[i] = block;
            }
            return result;
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            throw new Exception(e);
        }
    }

    public static String toStingBlocks(Block[] blocks) {
        return Arrays.toString(blocks);
    }
}
