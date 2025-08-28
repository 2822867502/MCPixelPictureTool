package org.zlk.mcpixelpicturetool.type.color;

import java.util.Objects;

public class SRGB implements Color {
    public final int a;
    public final int r;
    public final int g;
    public final int b;

    public SRGB(int a, int r, int g, int b) {
        this.a = a;
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public SRGB(int r, int g, int b) {
        this(0xff,r,g,b);
    }

    public SRGB(int rgb) {
//        this.a = (rgb & 0xff000000) >> 24;
//        this.r = (rgb & 0x00ff0000) >> 16;
//        this.g = (rgb & 0x0000ff00) >> 8;
//        this.b = (rgb & 0x000000ff);
        this.a = (rgb >> 24) & 0xff;
        this.r = (rgb >> 16) & 0xff;
        this.g = (rgb >> 8) & 0xff;
        this.b = rgb & 0xff;
        //你知道吗，这两种写法结果不一样，太悲催了
    }

    private static double toLinearRGB(double c) {
        return (c > 0.04045) ? Math.pow((c + 0.055) / 1.055, 2.4) : c / 12.92;
    }

    public XYZ toXYZ() {
        double r_d = toLinearRGB(r / 255.0);
        double g_d = toLinearRGB(g / 255.0);
        double b_d = toLinearRGB(b / 255.0);
        double x = r_d * 0.412453 + g_d * 0.357580 + b_d * 0.180423;
        double y = r_d * 0.212671 + g_d * 0.715160 + b_d * 0.072169;
        double z = r_d * 0.019334 + g_d * 0.119193 + b_d * 0.950227;
        return new XYZ(x, y, z);
    }

    public int toInt() {
        return ((a << 24) + (r << 16) + (g << 8) + b);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        SRGB srgb = (SRGB) object;
        return r == srgb.r && g == srgb.g && b == srgb.b;
    }

    public String toHex() {
        return String.format("#%02X%02X%02X", r, g, b);
    }

    @Override
    public int hashCode() {
        return Objects.hash(r, g, b);
    }
}
