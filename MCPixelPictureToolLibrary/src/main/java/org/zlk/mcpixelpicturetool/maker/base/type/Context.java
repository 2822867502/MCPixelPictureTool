package org.zlk.mcpixelpicturetool.maker.base.type;

import java.util.Hashtable;

public class Context extends Hashtable<String,Object> {
    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) super.get(key);
    }
    public void copy(String originKey,String targetKey) {
        if (this.containsKey(originKey)) {
            this.put(targetKey,this.get(originKey));
        }
    }

    public boolean containsKeys(String... keys) {
        for (String key: keys) {
            if (!containsKey(key)) return false;
        }
        return true;
    }
}
