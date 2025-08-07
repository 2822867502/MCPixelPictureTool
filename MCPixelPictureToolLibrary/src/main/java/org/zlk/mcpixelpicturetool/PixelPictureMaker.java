package org.zlk.mcpixelpicturetool;

import net.querz.nbt.tag.CompoundTag;
import org.zlk.mcpixelpicturetool.type.Block;
import org.zlk.mcpixelpicturetool.type.BlocksToPalettesMap;
import org.zlk.mcpixelpicturetool.type.color.Lab;
import org.zlk.mcpixelpicturetool.type.color.SRGB;
import org.zlk.mcpixelpicturetool.utils.ColorUtils;
import org.zlk.mcpixelpicturetool.utils.ImageUtils;
import org.zlk.mcpixelpicturetool.utils.NBTUtils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class PixelPictureMaker {
    public static final int MODE_PLAIN = 0;
    public static final int MODE_VERTICAL = 1;
    public static final int MODE_STAIR_DOWN = 2;
    public static final int PROCESS_RESIZE = 0;
    public static final int PROCESS_CAST_TO_LAB = 1;
    public static final int PROCESS_MAKE = 2;
    public static final int PROCESS_CONFORMITY = 3;
    public static final int PROCESS_END = 4;
    private static final int DEFAULT_DATA_VERSION = 0;
    private final BufferedImage image;
    private BufferedImage resizedImage;

    private BufferedImage previewImage;
    private BlocksToPalettesMap blocksToPalettesMap;
    private HashMap<Lab, Block> labToBlocks;
    private HashSet<CompoundTag> blocks;
    private int[] size;
    private CompoundTag result;
    private int process;

    public PixelPictureMaker(BufferedImage image) {
        this.image = image;
    }

    private void resize(int width, int height) {
        process = PROCESS_RESIZE;
        resizedImage = ImageUtils.resize(image, width, height);
        blocksToPalettesMap = new BlocksToPalettesMap();
    }

    private void castToLab(Block[] useBlocks) {
        process = PROCESS_CAST_TO_LAB;
        labToBlocks = new HashMap<>();
        Arrays.stream(useBlocks).forEach(b -> labToBlocks.put(ColorUtils.RGBToLab(b.srgb), b));
    }

    private void forEachPixel(int mode) {
        process = PROCESS_MAKE;
        blocks = new HashSet<>();
        int width = resizedImage.getWidth();
        int height = resizedImage.getHeight();
        previewImage = new BufferedImage(width, height, resizedImage.getType());
        //todo 多线程
        switch (mode) {
            case MODE_PLAIN: {
                size = new int[]{width, 1, height};
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        int finalY = y;
                        int finalX = x;
                        submitTask(() -> perPixel(finalX, finalY, finalX, 0, finalY));
                    }
                }
            }
            break;
            case MODE_VERTICAL: {
                size = new int[]{width, height, 1};
                for (int x = 0; x < width; x++) {
                    for (int y = height; y > 0; y--) {
                        int finalX = x;
                        int finalY = y;
                        submitTask(() -> perPixel(finalX, finalY, finalX, height - finalY - 1, 0));
                    }
                }
            }
            break;
            case MODE_STAIR_DOWN: {
                size = new int[]{width, height, height};
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        int finalX = x;
                        int finalY = y;
                        submitTask(() -> perPixel(finalX, finalY, finalX, height - finalY - 1, height - finalY - 1));
                    }
                }
            }
            break;
        }
        waitUntilAllTaskEnd();
    }

    private void conformityAndWrite() {
        process = PROCESS_CONFORMITY;
        result = NBTUtils.getRoot(
                NBTUtils.getSize(size[0], size[1], size[2]),
                NBTUtils.getVersion(DEFAULT_DATA_VERSION),
                NBTUtils.getBlocks(blocks),
                NBTUtils.getPalettesMap(blocksToPalettesMap)
        );
    }

    private void perPixel(int pixelX, int pixelY, int targetX, int targetY, int targetZ) {
        SRGB srgb = new SRGB(resizedImage.getRGB(pixelX, pixelY));
        Lab lab = ColorUtils.RGBToLab(srgb);
        Lab closeLab = ColorUtils.getCloseColor(labToBlocks.keySet(), lab);
        Block block = labToBlocks.get(closeLab);
        previewImage.setRGB(pixelX, pixelY, block.srgb.toInt());
        blocksToPalettesMap.put(block);
        blocks.add(NBTUtils.getBlockNBT(blocksToPalettesMap, block, targetX, targetY, targetZ));
    }

    private void submitTask(Runnable runnable) {
        runnable.run();
        //todo
    }

    private void waitUntilAllTaskEnd() {
        //todo
    }

    public void make(int width, int height, Block[] useBlocks, int mode) {
        resize(width, height);
        castToLab(useBlocks);
        forEachPixel(mode);
        conformityAndWrite();
        process = PROCESS_END;
    }

    public void write(File file) throws IOException {
        NBTUtils.write(result, file);
    }

    public int getProcess() {
        return process;
    }

    public BufferedImage getPreviewImage() {
        return previewImage;
    }
}
