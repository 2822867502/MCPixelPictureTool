package org.zlk.mcpixelpicturetool.maker.processor.picture;

import org.zlk.mcpixelpicturetool.maker.base.node.VirtualListProcessorNode;
import org.zlk.mcpixelpicturetool.maker.base.type.Context;
import org.zlk.mcpixelpicturetool.type.Block;
import org.zlk.mcpixelpicturetool.type.color.Lab;
import org.zlk.mcpixelpicturetool.utils.ColorUtils;

import java.util.Hashtable;

public class CastChoiceBlockToLabProcessor extends VirtualListProcessorNode<Block> {
    public static final String KEY_CHOICE_BLOCKS = "CastChoiceBlockToLab-ChoiceBlocks";
    public static final String KEY_RESULT = "CastChoiceBlockToLab-Result";
    private final Hashtable<Lab,Block> labToBlocksTable = new Hashtable<>();
    public CastChoiceBlockToLabProcessor() {
        super("CastChoiceBlockToLab", null);
        setDataProcessor(this::processData);
    }

    private void processData(Block block) throws Exception{
        labToBlocksTable.put(ColorUtils.RGBToLab(block.srgb), block);
    }

    @Override
    public void process(Context context) throws Exception {
        setInput(context.get(KEY_CHOICE_BLOCKS));
        super.process(context);
        context.put(KEY_RESULT, labToBlocksTable);
    }

    @Override
    public void clear() {
        labToBlocksTable.clear();
    }
}
