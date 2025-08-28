package org.zlk.mcpixelpicturetool.maker.processor.picture;

import org.zlk.mcpixelpicturetool.maker.base.node.VirtualListProcessorNode;
import org.zlk.mcpixelpicturetool.maker.base.type.Context;
import org.zlk.mcpixelpicturetool.type.Block;
import org.zlk.mcpixelpicturetool.type.Point;
import org.zlk.mcpixelpicturetool.type.color.Lab;
import org.zlk.mcpixelpicturetool.type.color.SRGB;
import org.zlk.mcpixelpicturetool.type.container.PointsContainer;
import org.zlk.mcpixelpicturetool.utils.ColorUtils;

import java.awt.image.BufferedImage;
import java.util.Hashtable;

public class GeneratePixelPictureProcessor extends VirtualListProcessorNode<GeneratePixelPictureProcessor.PixelProcessPackage> {
    public static final int MODE_PLAIN = 0;
    public static final int MODE_VERTICAL = 1;
    public static final int MODE_STAIR_DOWN = 2;
    //支持任意不透明SRGB类型和任意Block类型的策略
    public static final String STRATEGY_NOTHING = "nothing";
    public static final SRGB STRATEGY_COLOR_WHITE = new SRGB(255, 255, 255);
    public static final SRGB STRATEGY_COLOR_BLACK = new SRGB(0, 0, 0);
    public static final String KEY_USE_BLOCKS = "GeneratePixelPicture-UseBlocks";
    public static final String KEY_IMAGE = "GeneratePixelPicture-Image";
    public static final String KEY_MODE = "GeneratePixelPicture-Mode";
    public static final String KEY_TRANSPARENT_STRATEGY = "GeneratePixelPicture-TransparentStrategy";
    public static final String KEY_RESULT_BLOCK_POINTS = "GeneratePixelPicture-Result-BlockPoints";
    public static final String KEY_RESULT_PIXEL_POINTS = "GeneratePixelPicture-Result-PixelPoints";
    public static final String KEY_RESULT_SIZE = "GeneratePixelPicture-Result-Size";
    private final PixelPictureDataGenerator[] SUPPORTED_PROCESS_MODE = new PixelPictureDataGenerator[]{
            this::plainMode,
            this::verticalMode,
            this::stairDownMode
    };

    public static class PixelProcessPackage {
        public final Point pixelPoint;
        public final Point blockPoint;

        //这里就不做检查了，我大概不会犯这个低级错误
        private PixelProcessPackage(Point pixelPoint, Point blockPoint) {
            this.pixelPoint = pixelPoint;
            this.blockPoint = blockPoint;
        }
    }

    @FunctionalInterface
    public interface PixelPictureDataGenerator {
        PixelProcessPackage[] generateDatas();
    }

    private Hashtable<Lab, Block> useBlocks;
    private int mode;
    private BufferedImage image;
    private int targetWidth;
    private int targetHeight;
    private PointsContainer<Block> blockPoints;
    private PointsContainer<SRGB> pixelPoints;
    private Object transparentStrategy;

    private int[] size;

    public GeneratePixelPictureProcessor() {
        super("GeneratePixelPicture", null);
        setDataProcessor(this::processPixel);
    }

    @Override
    public void process(Context context) throws Exception {
        useBlocks = context.get(KEY_USE_BLOCKS);
        mode = context.get(KEY_MODE);
        image = context.get(KEY_IMAGE);
        targetWidth = image.getWidth();
        targetHeight = image.getHeight();
        blockPoints = new PointsContainer<>(3);
        pixelPoints = new PointsContainer<>(2);
        transparentStrategy = context.get(KEY_TRANSPARENT_STRATEGY);
        //todo 这里代码风格可能混乱，因为没写clear

        try {
            setInput(SUPPORTED_PROCESS_MODE[mode].generateDatas());
            //在这里会把要处理的数据添加进去
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Unsupported mode: " + mode);
        }

        super.process(context);

        context.put(KEY_RESULT_BLOCK_POINTS, blockPoints);
        context.put(KEY_RESULT_PIXEL_POINTS, pixelPoints);
        context.put(KEY_RESULT_SIZE, size);
    }

    private void processPixel(PixelProcessPackage processPackage) {
        Point pixelPoint = processPackage.pixelPoint;
        Point blockPoint = processPackage.blockPoint;
        SRGB srgb = new SRGB(image.getRGB(pixelPoint.getPoint(0), pixelPoint.getPoint(1)));
        Block block = processColorToBlock(srgb);
        if (block != null) {
            synchronized (blockPoint) {
                blockPoints.put(blockPoint, block);
            }
            synchronized (pixelPoint) {
                pixelPoints.put(pixelPoint, block.srgb);
            }
        }
    }

    private Block processColorToBlock(SRGB color) {
        if (color.a != 0x00) {
            Lab lab = ColorUtils.RGBToLab(color);
            Lab closeLab = ColorUtils.getCloseColor(useBlocks.keySet(), lab);
            return useBlocks.get(closeLab);
        } else {
            return processTransparentToBlock();
        }
    }

    private Block processTransparentToBlock() {
        if (transparentStrategy instanceof SRGB) {
            SRGB srgb = (SRGB) transparentStrategy;
            if (srgb.a == 0x00) throw new IllegalArgumentException("Unsupported strategy: Input a transparent color");
            return processColorToBlock(srgb);
        }
        if (transparentStrategy instanceof Block) {
            return (Block) transparentStrategy;
        }
        if (transparentStrategy instanceof String && STRATEGY_NOTHING.equals(transparentStrategy)) {
            return null;
        }
        throw new IllegalArgumentException("Unsupported strategy: Unsupported type: " + (transparentStrategy == null ? "null" : transparentStrategy.getClass().getName()));
    }


    private PixelProcessPackage[] plainMode() {
        blockPoints.clear();
        size = new int[]{targetWidth, 1, targetHeight};
        //应该不会有人尝试制作21亿像素以上的像素画吧
        PixelProcessPackage[] packages = new PixelProcessPackage[targetWidth * targetHeight];
        for (int y = 0; y < targetHeight; y++) {
            for (int x = 0; x < targetWidth; x++) {
                packages[y * targetWidth + x] = new PixelProcessPackage(
                        new Point(x, y),
                        new Point(x, 0, y)
                );
            }
        }
        return packages;
    }

    private PixelProcessPackage[] verticalMode() {
        blockPoints.clear();
        size = new int[]{targetWidth, targetHeight, 1};
        //应该不会有人尝试制作21亿像素以上的像素画吧
        PixelProcessPackage[] packages = new PixelProcessPackage[targetWidth * targetHeight];
        for (int y = 0; y < targetHeight; y++) {
            for (int x = 0; x < targetWidth; x++) {
                packages[y * targetWidth + x] = new PixelProcessPackage(
                        new Point(x, y),
                        new Point(x,targetHeight - y - 1, 0)
                );
            }
        }
        return packages;
    }

    private PixelProcessPackage[] stairDownMode() {
        blockPoints.clear();
        size = new int[]{targetWidth, targetHeight, targetHeight};
        //应该不会有人尝试制作21亿像素以上的像素画吧
        PixelProcessPackage[] packages = new PixelProcessPackage[targetWidth * targetHeight];
        for (int y = 0; y < targetHeight; y++) {
            for (int x = 0; x < targetWidth; x++) {
                packages[y * targetWidth + x] = new PixelProcessPackage(
                        new Point(x, y),
                        new Point(x, targetHeight - y - 1, targetHeight - y - 1)
                );
            }
        }
        return packages;
    }
}
