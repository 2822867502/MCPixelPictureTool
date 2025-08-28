package org.zlk.mcpixelpicturetool.type.container;

import org.zlk.mcpixelpicturetool.type.Point;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.Map;

public class PointsContainer<V> extends Hashtable<Point,V> {
    protected final int dimension;
    protected int[] size;
    protected PointsContainer(PointsContainer<V> container) {
        super(container);
        dimension = container.dimension;
        size = container.size;
    }

    public PointsContainer(int dimension) {
        this.dimension = dimension;
        size = new int[dimension];
    }
    private void checkPointDimension(int[] input) {
        if (input.length != dimension) throw new IllegalArgumentException(String.format("Point: %s is not a %d dimension point", Arrays.toString(input), dimension));
    }
    private void checkSizeDimension(int[] input) {
        if (input.length != dimension) throw new IllegalArgumentException(String.format("Point: %s is not a %d dimension size", Arrays.toString(input), dimension));
    }
    private void checkAndUpdateSize(int[] input) {
        for (int i = 0; i < size.length ; i++) {
            size[i] = Math.max(size[i],input[i] + 1);
        }
    }
    private void checkSetSize(int[] input) {
        for (int i = 0; i < size.length ; i++) {
            if (size[i] > input[i]) throw new IllegalArgumentException(String.format("Point: %s is smaller than %s", Arrays.toString(input),Arrays.toString(size)));
        }
    }
    @Override
    public V get(Object key) {
        if (key instanceof int[]) checkPointDimension((int[]) key);
        return super.get(key);
    }

    @Override
    public boolean containsKey(Object key) {
        if (key instanceof int[]) checkPointDimension((int[]) key);
        return super.containsKey(key);
    }

    @Override
    public V put(Point key, V value) {
        checkPointDimension(key.getPoint());
        checkAndUpdateSize(key.getPoint());
        return super.put(key, value);
    }

    @Override
    public void putAll(Map<? extends Point, ? extends V> m) {
        m.keySet().stream().map(Point::getPoint).forEach(this::checkPointDimension);
        m.keySet().stream().map(Point::getPoint).forEach(this::checkAndUpdateSize);
        super.putAll(m);
    }

    @Override
    public V remove(Object key) {
        if (key instanceof int[]) checkPointDimension((int[]) key);
        return super.remove(key);
    }
    public void setSize(int[] size) {
        checkSizeDimension((size));
        checkSetSize(size);
        this.size = size;
    }

    public int[] getSize() {
        return size;
    }
}
