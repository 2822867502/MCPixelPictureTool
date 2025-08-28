package org.zlk.mcpixelpicturetool.type;

import org.junit.jupiter.api.Test;
import org.zlk.mcpixelpicturetool.type.color.Lab;
import org.zlk.mcpixelpicturetool.type.color.SRGB;
import org.zlk.mcpixelpicturetool.type.color.XYZ;
import org.zlk.mcpixelpicturetool.utils.ColorUtils;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ColorTest {
    @Test
    void test1() {
        SRGB srgb = new SRGB(134, 84, 62);
        XYZ xyz = srgb.toXYZ();
        Lab lab = xyz.toLab();
        assertEquals(lab, new Lab(40.8288361517098, 18.300888761837996, 21.471768442507987));
        assertEquals(lab, ColorUtils.RGBToLab(134, 84, 62));
    }

    @Test
    void test2() {
        assertEquals(ColorUtils.deltaE2000(new Lab(17.7900, 7.9800, 11.1100), new Lab(37.5420, 12.0180, 13.3300)), 15.288151598916844);
    }

    @Test
    void test3() {
        HashSet<Lab> colorSet = new HashSet<>();
        colorSet.add(new Lab(50, 2, 2));      // 中等亮度，偏红
        colorSet.add(new Lab(80, -2, -2));    // 高亮度，偏绿蓝
        colorSet.add(new Lab(20, 0, 0));      // 低亮度，中性
        colorSet.add(new Lab(50, -40, 40));   // 高饱和度，偏青紫
        colorSet.add(new Lab(50, 40, -40));   // 高饱和度，偏红绿
        assertEquals(ColorUtils.getCloseColor(
                colorSet,
                new Lab(49.9999,39.999,-40)
        ),new Lab(50, 40, -40));
        assertEquals(ColorUtils.getCloseColor(
                colorSet,
                new Lab(49.9999,-39.999,40)
        ),new Lab(50, -40, 40));
    }
}