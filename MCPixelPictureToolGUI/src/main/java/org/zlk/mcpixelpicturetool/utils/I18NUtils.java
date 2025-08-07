package org.zlk.mcpixelpicturetool.utils;

import org.zlk.mcpixelpicturetool.type.property.KeyValueI18NProperty;
import org.zlk.mcpixelpicturetool.type.property.ParseException;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;

public class I18NUtils {
    private static final ResourceBundle guiBundle = ResourceBundle.getBundle("res", getLocale());
    private static final ResourceBundle dataBundle = ResourceBundle.getBundle("data", getLocale());
    private static final ResourceBundle blocksBundle = ResourceBundle.getBundle("blocks", getLocale());

    public static Locale getLocale() {
        return Locale.getDefault();
    }

    public static String getGUIString(String key) {
        return guiBundle.getString(key);
    }

    public static String getGUIString(String key, String... args) {
        return String.format(guiBundle.getString(key), (Object[]) args);
    }

    public static String getDataString(String key) {
        return dataBundle.getString(key);
    }

    public static KeyValueI18NProperty getData(String key) {
        try {
            return KeyValueI18NProperty.parse(getDataString(key));
        } catch (ParseException e) {
            //当出现这种问题，责任在代码，因此不必处理，直接抛
            throw new RuntimeException(e);
        }
    }

    public static String[] getDataStrings(String key) {
        String string = getDataString(key).replace("[", "").replace("]", "").replace(" ", "");
        if (string.isEmpty()) return new String[0];
        return string.split(",");
    }

    public static KeyValueI18NProperty[] getDatas(String key) {
        return Arrays.stream(getDataStrings(key)).map((String string) -> {
            try {
                return KeyValueI18NProperty.parse(string);
            } catch (ParseException e) {
                //当出现这种问题，责任在代码，因此不必处理，直接抛
                throw new RuntimeException(e);
            }
        }).toArray(KeyValueI18NProperty[]::new);
    }

    public static String getBlockString(String key) {
        return blocksBundle.getString(key);
    }

    public static KeyValueI18NProperty getBlock(String key) {
        try {
            return KeyValueI18NProperty.parse(getBlockString(key));
        } catch (ParseException e) {
            //当出现这种问题，责任在代码，因此不必处理，直接抛
            throw new RuntimeException(e);
        }
    }

    public static String[] getBlockStrings(String key) {
        String string = getBlockString(key).replace("[", "").replace("]", "").replace(" ", "");
        if (string.isEmpty()) return new String[0];
        return string.split(",");
    }

    public static KeyValueI18NProperty[] getBlocks(String key) {
        return Arrays.stream(getBlockStrings(key)).map((String string) -> {
            try {
                return KeyValueI18NProperty.parse(string);
            } catch (ParseException e) {
                //当出现这种问题，责任在代码，因此不必处理，直接抛
                throw new RuntimeException(e);
            }
        }).toArray(KeyValueI18NProperty[]::new);
    }

    public static InputStream getResources(String name) {
        return I18NUtils.class.getClassLoader().getResourceAsStream(name);
    }
}
