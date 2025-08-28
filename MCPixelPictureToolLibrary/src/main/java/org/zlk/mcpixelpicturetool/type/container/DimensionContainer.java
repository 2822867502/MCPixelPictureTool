package org.zlk.mcpixelpicturetool.type.container;

import org.omg.CORBA.portable.Streamable;
import org.zlk.mcpixelpicturetool.type.color.Color;

import java.util.stream.Stream;

public class DimensionContainer<T extends Color>{
    private final Object[][] objects;
    public DimensionContainer(int xLength, int yLength){
        objects = new Color[xLength][yLength];
    }
    public void set(T object,int x,int y) {
        objects[y][x] = object;
    }
    @SuppressWarnings("unchecked")
    public T get(int x, int y) {
        return (T) objects[y][x];
    }
    public int getXLength() {
        return objects[0].length;
    }
    public int getYLength() {
        return objects.length;
    }
}
