package org.zlk.mcpixelpicturetool.maker.processor.picture;

import org.zlk.mcpixelpicturetool.maker.base.abstracts.ProcessorNode;
import org.zlk.mcpixelpicturetool.maker.base.interfaces.Processor;
import org.zlk.mcpixelpicturetool.maker.base.node.SimpleProcessorNode;
import org.zlk.mcpixelpicturetool.maker.base.type.Context;
import org.zlk.mcpixelpicturetool.utils.ImageUtils;

import java.awt.image.BufferedImage;

public class ResizeImageProcessor extends ProcessorNode {
    public static final String KEY_IMAGE = "ResizeImage-Image";
    public static final String KEY_WIDTH = "ResizeImage-Width";
    public static final String KEY_HEIGHT = "ResizeImage-Height";
    public static final String KEY_RESULT = "ResizeImage-Result";
    public ResizeImageProcessor() {
        super("ResizeImage");
    }

    @Override
    public void process(Context context) throws Exception {
        BufferedImage image = context.get(KEY_IMAGE);
        int width = context.get(KEY_WIDTH);
        int height = context.get(KEY_HEIGHT);
        context.put(KEY_RESULT,ImageUtils.resize(image, width, height));
    }

    @Override
    public void clear() {
        //do nothing
    }
}
