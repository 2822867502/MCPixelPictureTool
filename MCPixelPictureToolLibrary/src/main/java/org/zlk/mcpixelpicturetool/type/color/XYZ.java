package org.zlk.mcpixelpicturetool.type.color;

import java.util.Objects;

public class XYZ implements Color {
    private static final double Xn = 0.950456;
    private static final double Yn = 1.000000;
    private static final double Zn = 1.088754;
    public final double x;
    public final double y;
    public final double z;
    public XYZ(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    private static double XYZValueFunction(double v) {
        return v > 0.008856 ? Math.pow(v, 1.0 / 3) : 7.787 * v + 16.0 / 116;
    }

    public Lab toLab() {
        double x_divide_xn_f = XYZValueFunction(x / Xn);
        double y_divide_yn_f = XYZValueFunction(y / Yn);
        double z_divide_zn_f = XYZValueFunction(z / Zn);
        double L = 116 * y_divide_yn_f - 16;
        double a = 500 * (x_divide_xn_f - y_divide_yn_f);
        double b = 200 * (y_divide_yn_f - z_divide_zn_f);
        return new Lab(L, a, b);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        XYZ XYZ = (XYZ) object;
        return Double.compare(x, XYZ.x) == 0 && Double.compare(y, XYZ.y) == 0 && Double.compare(z, XYZ.z) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }
}
