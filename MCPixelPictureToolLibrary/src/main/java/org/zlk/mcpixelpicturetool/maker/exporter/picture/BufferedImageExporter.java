package org.zlk.mcpixelpicturetool.maker.exporter.picture;

import org.zlk.mcpixelpicturetool.maker.base.ability.Castable;
import org.zlk.mcpixelpicturetool.maker.base.abstracts.AbstractExporter;
import org.zlk.mcpixelpicturetool.maker.base.interfaces.Generator;
import org.zlk.mcpixelpicturetool.type.Point;
import org.zlk.mcpixelpicturetool.type.color.SRGB;
import org.zlk.mcpixelpicturetool.type.container.PointsContainer;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class BufferedImageExporter extends AbstractExporter<PointsContainer<SRGB>, BufferedImage> {
    public BufferedImageExporter(String name, Generator generator) {
        super(name,generator);
    }

    protected int getXLength() {
        return input.getSize()[0];
    }
    protected int getYLength() {
        return input.getSize()[1];
    }
    protected int getX(Point point) {
        return point.getPoint(0);
    }
    protected int getY(Point point) {
        return point.getPoint(1);
    }

    protected Set<Map.Entry<Point, SRGB>> getAllPoints() {
        return input.entrySet();
    }

    @Override
    public void export() throws IOException {
        output = new BufferedImage(getXLength(),getYLength(),BufferedImage.TYPE_INT_ARGB);
        getAllPoints().forEach((e) -> {
            Point point = Objects.requireNonNull(e.getKey(),"Point is null");
            SRGB srgb = Objects.requireNonNull(e.getValue(),"SRGB is null");
            output.setRGB(getX(point),getY(point),srgb.toInt());
            //使用null作为不添加像素的标记，是偷懒而富含bug的，干脆严格判断null
        });
    }
}
