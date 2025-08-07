package org.zlk.mcpixelpicturetool.type.color;

import java.util.Objects;

public class Lab implements Color {
    public final double L;
    public final double a;
    public final double b;

    public Lab(double l, double a, double b) {
        L = l;
        this.a = a;
        this.b = b;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Lab lab = (Lab) object;
        return Double.compare(L, lab.L) == 0 && Double.compare(a, lab.a) == 0 && Double.compare(b, lab.b) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(L, a, b);
    }
}
