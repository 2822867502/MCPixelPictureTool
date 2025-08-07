package org.zlk.mcpixelpicturetool.type;

import org.zlk.mcpixelpicturetool.utils.BlocksUtils;

import javax.swing.*;
import java.text.ParseException;

public class BlocksFormatter extends JFormattedTextField.AbstractFormatter {
    @Override
    public Object stringToValue(String text) throws ParseException {
        try {
            return BlocksUtils.parseBlocks(text);
        } catch (Exception e) {
            throw new ParseException(e.getMessage(), 0);
            //todo 我真懒得写了，以后再优化吧
        }
    }

    @Override
    public String valueToString(Object value) throws ParseException {
        if (value == null) return "";
        try {
            return BlocksUtils.toStingBlocks((Block[]) value);
        } catch (Exception e) {
            throw new ParseException(e.getMessage(), 0);
        }
    }
}
