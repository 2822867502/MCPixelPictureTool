package org.zlk.mcpixelpicturetool.maker.loader.picture;

import org.zlk.mcpixelpicturetool.maker.base.abstracts.AbstractLoader;
import org.zlk.mcpixelpicturetool.maker.base.interfaces.Generator;
import org.zlk.mcpixelpicturetool.utils.ImageUtils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageFileLoader extends AbstractLoader<File,BufferedImage> {
    public ImageFileLoader(String name, Generator generator) {
        super(name,generator);
    }

    @Override
    public void load() throws IOException {
        output = ImageUtils.fromFile(input);
    }
}
