package org.zlk.mcpixelpicturetool.type.property;

import java.util.Objects;

public class KeyValueI18NProperty {
    public final String key;
    public final String value;
    public final String i18N;

    public KeyValueI18NProperty(String key, String value, String i18N) {
        this.key = key;
        this.value = value;
        this.i18N = i18N;
    }

    public static KeyValueI18NProperty parse(String string) throws ParseException {
        String[] group = string.split("&");
        if (group.length != 2) throw new ParseException("Not a valid property");
        String[] kv = group[0].split("=");
        if (kv.length != 2) throw new ParseException("Not contain key-value");
        return new KeyValueI18NProperty(kv[0], kv[1], group[1]);
    }

    public String toKVString() {
        return key + "=" + value;
    }//我也想把Block扔掉，但是谁知道写了两天的项目还能充满史山

    @Override
    public String toString() {
        return toKVString() + "&" + i18N;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        KeyValueI18NProperty that = (KeyValueI18NProperty) object;
        return Objects.equals(key, that.key) && Objects.equals(value, that.value) && Objects.equals(i18N, that.i18N);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value, i18N);
    }
}
