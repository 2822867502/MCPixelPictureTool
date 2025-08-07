package org.zlk.mcpixelpicturetool.type;

import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.StringTag;
import org.zlk.mcpixelpicturetool.type.color.SRGB;

import java.util.Objects;

public class Block {
    public final String name;
    public final SRGB srgb;

    public Block(String name, SRGB srgb) {
        this.name = name;
        this.srgb = srgb;
    }

    @Override
    public String toString() {
        return String.format("%s=%s", name, srgb.toHex());
    }

    public CompoundTag toCompoundTag() {
        CompoundTag result = new CompoundTag();
        result.put("Name", new StringTag(name));
        //todo 特殊状态支持
        return result;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Block block = (Block) object;
        return Objects.equals(name, block.name) && Objects.equals(srgb, block.srgb);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, srgb);
    }
}
