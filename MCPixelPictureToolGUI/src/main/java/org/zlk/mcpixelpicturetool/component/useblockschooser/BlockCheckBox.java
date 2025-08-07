package org.zlk.mcpixelpicturetool.component.useblockschooser;

import org.zlk.mcpixelpicturetool.type.Block;
import org.zlk.mcpixelpicturetool.type.property.KeyValueI18NProperty;
import org.zlk.mcpixelpicturetool.utils.BlocksUtils;

import javax.swing.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.HashSet;

public class BlockCheckBox extends JCheckBox {
    private HashSet<Block> selectedBlocks;
    private KeyValueI18NProperty block;
    private Block blockObj;

    public BlockCheckBox() {
        super();
        addItemListener(e -> {
            if (isSelected()) {
                selectedBlocks.add(blockObj);
            } else {
                selectedBlocks.remove(blockObj);
            }
        });
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                refreshSelect();
            }
        });
    }

    @Override
    public void setSelected(boolean b) {
        if (b) {
            selectedBlocks.add(blockObj);
        } else {
            selectedBlocks.remove(blockObj);
        }
        super.setSelected(b);
    }

    public void setBlock(KeyValueI18NProperty block) {
        this.block = block;
        this.blockObj = BlocksUtils.parseBlock(block.toKVString());
        setText(block.i18N);
    }

    public void setSelectedBlocks(HashSet<Block> selectedBlocks) {
        this.selectedBlocks = selectedBlocks;
    }

    public void refreshSelect() {
        setSelected(selectedBlocks != null && selectedBlocks.contains(blockObj));
    }

}
