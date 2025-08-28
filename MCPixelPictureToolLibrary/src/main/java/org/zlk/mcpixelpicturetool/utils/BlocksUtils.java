package org.zlk.mcpixelpicturetool.utils;

import org.zlk.mcpixelpicturetool.type.Block;

import java.util.ArrayList;
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
            String blockString;
            if (!string.matches("\\[.*\\]")) {
                return new Block[0];
            } else {
                blockString = string.substring(
                        string.indexOf('[') + 1,
                        string.indexOf(']')
                ).trim();
            }

            if (blockString.isEmpty()) return new Block[0];
            String[] blockStrings = blockString.split(",");
            ArrayList<Block> result = new ArrayList<>();
            //不能用数组，否则有默认初始化值为null的问题
            for (String s : blockStrings) {
                Block block = parseBlock(s);
                if (block == null) continue;
                result.add(block);
            }
            return result.toArray(new Block[0]);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            throw new Exception(e);
        }
    }

    public static String toStingBlocks(Block[] blocks) {
        return Arrays.toString(blocks);
    }
}
