package org.zlk.mcpixelpicturetool.type;

import java.util.Arrays;

public class Point {
    private final int[] point;
    public Point(int... point) {
        this.point = point;
    }
    public int dimension() {
        return point.length;
    }
    public int[] getPoint() {
        return point;
    }
    public int getPoint(int dimension) {
        return getPoint()[dimension];
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Point point1 = (Point) object;
        return Arrays.equals(point, point1.point);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(point);
    }
}
