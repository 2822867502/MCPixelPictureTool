package org.zlk.mcpixelpicturetool.utils;

import org.zlk.mcpixelpicturetool.type.color.SRGB;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class ImageUtils {
    public static BufferedImage fromFile(File file) throws IOException {
        return ImageIO.read(file);
    }

    public static BufferedImage fromStream(InputStream inputStream) throws IOException {
        return ImageIO.read(inputStream);
    }

    public static boolean toFile(File file, BufferedImage image, String format) throws IOException {
        return ImageIO.write(image, format, file);
    }

    public static BufferedImage resize(BufferedImage image, int w, int h) {
        //压缩像素至指定像素
        Image newImage = resize((Image) image, w, h);
        //转换对象
        BufferedImage bufferedImage = new BufferedImage(w, h, image.getType());
        Graphics2D g2d = bufferedImage.createGraphics();
        g2d.drawImage(newImage, 0, 0, null);
        g2d.dispose();
        return bufferedImage;
    }

    public static Image resize(Image image, int w, int h) {
        return image.getScaledInstance(w, h, Image.SCALE_SMOOTH);
    }

    public static Image resize(Image image, ImageObserver observer, double rate) {
        return resize(image, (int) (image.getWidth(observer) * rate), (int) (image.getHeight(observer) * rate));
    }

    public static BufferedImage resize(BufferedImage image, double rate) {
        return resize(image, (int) (image.getWidth() * rate), (int) (image.getHeight() * rate));
    }

    public static SRGB getMostOccurColor(BufferedImage image) {
        int maxX = image.getWidth();
        int maxY = image.getHeight();
        HashMap<SRGB, Integer> colorMap = new HashMap<>();
        SRGB mostOccurColor = null;
        int mostOccurColorFrequency = 0;
        for (int x = 0; x < maxX; x++) {
            for (int y = 0; y < maxY; y++) {
                SRGB color = new SRGB(image.getRGB(x, y));
                Integer c = colorMap.getOrDefault(color, 0);
                c++;
                if (c > mostOccurColorFrequency) {
                    mostOccurColor = color;
                    mostOccurColorFrequency = c;
                }
                colorMap.put(color, c);
            }
        }
        return mostOccurColor;

    }
}
