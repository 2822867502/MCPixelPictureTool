package org.zlk.mcpixelpicturetool.maker.generator.picture;

import net.querz.nbt.tag.CompoundTag;
import org.zlk.mcpixelpicturetool.maker.ProcessorManager;
import org.zlk.mcpixelpicturetool.maker.base.abstracts.AbstractGenerator;
import org.zlk.mcpixelpicturetool.maker.base.node.SimpleProcessorNode;
import org.zlk.mcpixelpicturetool.maker.base.type.Chain;
import org.zlk.mcpixelpicturetool.maker.base.type.Context;
import org.zlk.mcpixelpicturetool.maker.base.type.UseThreadCalculator;
import org.zlk.mcpixelpicturetool.maker.exporter.picture.BufferedImageExporter;
import org.zlk.mcpixelpicturetool.maker.exporter.picture.NBTTagExporter;
import org.zlk.mcpixelpicturetool.maker.loader.picture.ImageFileLoader;
import org.zlk.mcpixelpicturetool.maker.processor.picture.CastChoiceBlockToLabProcessor;
import org.zlk.mcpixelpicturetool.maker.processor.picture.GeneratePixelPictureProcessor;
import org.zlk.mcpixelpicturetool.maker.processor.picture.ResizeImageProcessor;
import org.zlk.mcpixelpicturetool.type.Block;
import org.zlk.mcpixelpicturetool.type.NBTTagOriginData;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class PixelPictureGenerator extends AbstractGenerator {
    public static final String NAME_GENERATOR = "PixelPictureGenerator";
    public static final String NAME_LOADER_IMAGE_FILE = "ImageFileLoader";
    public static final String NAME_EXPORTER_IMAGE = "ImageExporter";
    public static final String NAME_EXPORTER_NBT_TAG = "NBTTagExporter";
    private final Context context = new Context();
    private final Chain generatorChain;
    private final ProcessorManager manager;
    private final CastChoiceBlockToLabProcessor castChoiceBlockToLabProcessor = new CastChoiceBlockToLabProcessor();
    private final GeneratePixelPictureProcessor generatePixelPictureProcessor = new GeneratePixelPictureProcessor();
    private final ResizeImageProcessor resizeImageProcessor = new ResizeImageProcessor();

    public PixelPictureGenerator(ProcessorManager manager) {
        super(NAME_GENERATOR);
        addLoader(new ImageFileLoader(NAME_LOADER_IMAGE_FILE,this));

        this.manager = manager;
        generatorChain = new Chain(NAME_GENERATOR,
                Chain.createProcessorChainHead(
                        new SimpleProcessorNode("ProcessDataLoad",(c) -> c.put(ResizeImageProcessor.KEY_IMAGE,getOriginImageOrLoad())),
                        resizeImageProcessor,
                        new SimpleProcessorNode("ProcessDataCopy",(c) -> c.copy(ResizeImageProcessor.KEY_RESULT,GeneratePixelPictureProcessor.KEY_IMAGE)),
                        castChoiceBlockToLabProcessor,
                        new SimpleProcessorNode("ProcessDataCopy",(c) -> c.copy(CastChoiceBlockToLabProcessor.KEY_RESULT,GeneratePixelPictureProcessor.KEY_USE_BLOCKS)),
                        generatePixelPictureProcessor,
                        new SimpleProcessorNode("ProcessDataExport", (c) -> {
                            ((BufferedImageExporter) getExporter(NAME_EXPORTER_IMAGE)).setInput(c.get(GeneratePixelPictureProcessor.KEY_RESULT_PIXEL_POINTS));
                            NBTTagOriginData data = new NBTTagOriginData(
                                    c.get(GeneratePixelPictureProcessor.KEY_RESULT_SIZE),
                                    NBTTagOriginData.DEFAULT_DATA_VERSION,
                                    c.get(GeneratePixelPictureProcessor.KEY_RESULT_BLOCK_POINTS)
                            );
                            ((NBTTagExporter) getExporter(NAME_EXPORTER_NBT_TAG)).setInput(data);
                        })
                ),
                context
        );
        manager.addChain(generatorChain);

        addExporter(new BufferedImageExporter(NAME_EXPORTER_IMAGE,this));
        addExporter(new NBTTagExporter(NAME_EXPORTER_NBT_TAG,this));
    }
    public void setTargetHeight(int height) {
        context.put(ResizeImageProcessor.KEY_HEIGHT,height);
        dataChanged();
    }

    public void setTargetWidth(int width) {
        context.put(ResizeImageProcessor.KEY_WIDTH,width);
        dataChanged();
    }
    public void setChoiceBlocks(Block[] choiceBlocks) {
        context.put(CastChoiceBlockToLabProcessor.KEY_CHOICE_BLOCKS,choiceBlocks);
        dataChanged();
    }
    public void setAndLoadOriginImageFile(File file) throws IOException {
        ((ImageFileLoader) getLoader(NAME_LOADER_IMAGE_FILE)).setInput(file);
        ((ImageFileLoader) getLoader(NAME_LOADER_IMAGE_FILE)).load();
        dataChanged();
    }
    public void setCastChoiceBlockToLabUseThreadCalculator(UseThreadCalculator calculator) {
        castChoiceBlockToLabProcessor.setCalculator(calculator);
        dataChanged();
    }
    public void setGeneratePixelPictureUseThreadCalculator(UseThreadCalculator calculator) {
        generatePixelPictureProcessor.setCalculator(calculator);
        dataChanged();
    }

    public void setMode(int mode) {
        context.put(GeneratePixelPictureProcessor.KEY_MODE,mode);
        dataChanged();
    }
    public void setTransparentStrategy(Object strategy) {
        context.put(GeneratePixelPictureProcessor.KEY_TRANSPARENT_STRATEGY,strategy);
        dataChanged();
    }
    private void dataChanged() {
        clear();
    }
    public BufferedImage getOriginImageOrLoad() {
        if (!getLoader(NAME_LOADER_IMAGE_FILE).isLoaded()) {
            try {
                getLoader(NAME_LOADER_IMAGE_FILE).load();
            } catch (IOException e) {
                manager.getExceptionHandler().handle(e);
            }
        }
        return (BufferedImage) getLoader(NAME_LOADER_IMAGE_FILE).getOutput();
    }
    public BufferedImage getPreviewImageOrExport() {
        if(!getExporter(NAME_EXPORTER_IMAGE).isExported()) {
            try {
                getExporter(NAME_EXPORTER_IMAGE).export();
            } catch (IOException e) {
                manager.getExceptionHandler().handle(e);
            }
        }
        return (BufferedImage) getExporter(NAME_EXPORTER_IMAGE).getOutput();
    }
    public boolean canGenerate() {
        boolean result = context.containsKeys(
                ResizeImageProcessor.KEY_WIDTH,
                ResizeImageProcessor.KEY_HEIGHT,
                CastChoiceBlockToLabProcessor.KEY_CHOICE_BLOCKS,
                GeneratePixelPictureProcessor.KEY_MODE,
                GeneratePixelPictureProcessor.KEY_TRANSPARENT_STRATEGY
        );
        result &= getLoader(NAME_LOADER_IMAGE_FILE).isLoaded();
        return result;
    }
    @Override
    public void generate() {
        try {
            if (!canGenerate()) throw new IllegalArgumentException("Missing parameters, keys: " + context.keySet());
            manager.clear(NAME_GENERATOR);
            manager.processAwait(NAME_GENERATOR,true,true);
            generated = true;
        } catch (RuntimeException e) {
            manager.getExceptionHandler().handle(e);
        }
    }
    public CompoundTag getNBT() {
        ((NBTTagExporter) getExporter(NAME_EXPORTER_NBT_TAG)).export();
        return ((NBTTagExporter) getExporter(NAME_EXPORTER_NBT_TAG)).getOutput();
    }

    @Override
    public void clearAll() {
        super.clearAll();
        generatePixelPictureProcessor.clear();
        castChoiceBlockToLabProcessor.clear();
        resizeImageProcessor.clear();
        //todo 短短两个版本已经出史山了
    }
}
