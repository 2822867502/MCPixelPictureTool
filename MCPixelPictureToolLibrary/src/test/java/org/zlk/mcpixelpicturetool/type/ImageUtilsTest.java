package org.zlk.mcpixelpicturetool.type;

import org.junit.jupiter.api.Test;
import org.zlk.mcpixelpicturetool.type.color.SRGB;
import org.zlk.mcpixelpicturetool.utils.ImageUtils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

class ImageUtilsTest {
    @Test
    void test() throws IOException {
        BufferedImage image = ImageUtils.fromFile(new File("src/test/resources/1.jpeg"));
        System.out.printf("W: %d,H: %d%n", image.getWidth(), image.getHeight());
//        BufferedImage nImage1 = ImageUtils.resize(image,128,128);
//        BufferedImage nImage2 = ImageUtils.resize(image,512,512);
//        ImageUtils.toFile(new File("1_128.jpeg"),nImage1,"png");
//        ImageUtils.toFile(new File("1_512.jpeg"),nImage2,"jpg");
        BufferedImage image1 = ImageUtils.fromFile(new File("src/test/resources/1.jpeg"));
        ImageUtils.toFile(new File("src/test/resources/icon_512.png"), ImageUtils.resize(image1, 512, 512), "png");
    }

    @Test
    void test1() throws IOException {
        BufferedImage image = ImageUtils.fromFile(new File("src/test/resources/1.jpeg"));
        SRGB srgb = ImageUtils.getMostOccurColor(image);
        System.out.println(srgb.r + " " + srgb.g + " " + srgb.b);

    }
}