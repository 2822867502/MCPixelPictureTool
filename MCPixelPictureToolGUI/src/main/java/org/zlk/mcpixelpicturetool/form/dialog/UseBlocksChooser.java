package org.zlk.mcpixelpicturetool.form.dialog;

import org.zlk.mcpixelpicturetool.component.useblockschooser.CategoryPanel;
import org.zlk.mcpixelpicturetool.type.Block;
import org.zlk.mcpixelpicturetool.type.property.KeyValueI18NProperty;
import org.zlk.mcpixelpicturetool.type.property.ParseException;
import org.zlk.mcpixelpicturetool.utils.BlocksUtils;
import org.zlk.mcpixelpicturetool.utils.I18NUtils;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.Method;
import java.util.*;

public class UseBlocksChooser extends JDialog {
    public static final int CANCEL_OPTION = 1;
    public static final int APPROVE_OPTION = 0;
    public static final int ERROR_OPTION = -1;
    private final HashSet<Block> selectedBlocks = new HashSet<>();
    private final HashMap<KeyValueI18NProperty, KeyValueI18NProperty[]> supportedBlocks = new HashMap<>();
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTabbedPane categoriesPane;
    private int result;
    private boolean showed;

    public UseBlocksChooser(JFrame owner, Set<Block> selectedBlocks) {
        super(owner);
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(e -> onOK());

        buttonCancel.addActionListener(e -> onCancel());

        // 点击 X 时调用 onCancel()
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // 遇到 ESCAPE 时调用 onCancel()
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        this.selectedBlocks.addAll(selectedBlocks);

        createDialogContent();
    }

    private void createDialogContent() {
        loadSupportedBlocks();
        addCategoriesAndOtherCategories();
    }

    private void loadSupportedBlocks() {
        KeyValueI18NProperty[] categories = I18NUtils.getBlocks("supported.categories");
        Arrays.stream(categories).forEach(c -> supportedBlocks.put(c, I18NUtils.getBlocks(c.value)));
    }

    private void addCategoriesAndOtherCategories() {
        @SuppressWarnings("unchecked")
        HashSet<Block> selectedBlocksClone = (HashSet<Block>) selectedBlocks.clone();
        KeyValueI18NProperty unknownCategory = null;
        for (KeyValueI18NProperty c : supportedBlocks.keySet()) {
            if ("unknown".equals(c.key)) {//todo 拒绝硬编码，使用配置文件
                unknownCategory = c;
                continue;
            }
            addPerCategory(c, selectedBlocksClone);
        }
        //最后添加无类别的，不然乱乱的
        if (unknownCategory != null) {
            addPerCategory(unknownCategory, selectedBlocksClone);
        }
        //给不支持但是框里有的加进去
        //尽管这是loadSupportedBlocks干的活，但是在这干更合适
        if (!selectedBlocksClone.isEmpty()) {
            KeyValueI18NProperty otherCategory = I18NUtils.getBlock("supported.categories.other");
            KeyValueI18NProperty[] otherSupported = selectedBlocksClone
                    .stream()
                    .map(b -> {
                        try {
                            return KeyValueI18NProperty.parse(b.toString() + "&" + b.name);
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                            //当出现这种问题，责任在代码，因此不必处理，直接抛
                        }
                    })
                    .toArray(KeyValueI18NProperty[]::new);
            supportedBlocks.put(otherCategory, otherSupported);
            addPerCategory(otherCategory, selectedBlocksClone);
        }
    }

    private void addPerCategory(KeyValueI18NProperty category, HashSet<Block> selectedBlocksClone) {
        KeyValueI18NProperty[] blocks = supportedBlocks.get(category);
        CategoryPanel panel = new CategoryPanel();
        for (KeyValueI18NProperty b : blocks) {
            selectedBlocksClone.remove(BlocksUtils.parseBlock(b.toKVString()));
            panel.addBlock(b, selectedBlocks);
        }
        categoriesPane.addTab(category.i18N, panel);
    }

    private void onOK() {
        // 在此处添加您的代码
        result = APPROVE_OPTION;
        dispose();
    }

    private void onCancel() {
        result = CANCEL_OPTION;
        dispose();
    }

    public int showDialog() {
        if (showed) {
            result = ERROR_OPTION;
            return result;
        }
        pack();
        setMinimumSize(getSize());
        setVisible(true);
        showed = true;
        return result;
    }

    public Block[] getSelectedBlocks() {
        return selectedBlocks.toArray(new Block[0]);
    }

    private static Method $$$cachedGetBundleMethod$$$ = null;

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        contentPane = new JPanel();
        contentPane.setLayout(new GridBagLayout());
        contentPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridBagLayout());
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.VERTICAL;
        contentPane.add(panel1, gbc);
        buttonOK = new JButton();
        this.$$$loadButtonText$$$(buttonOK, this.$$$getMessageFromBundle$$$("res", "gui.useblockschooser.ok.button"));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(buttonOK, gbc);
        buttonCancel = new JButton();
        this.$$$loadButtonText$$$(buttonCancel, this.$$$getMessageFromBundle$$$("res", "gui.useblockschooser.cancel.button"));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(buttonCancel, gbc);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        contentPane.add(panel2, gbc);
        categoriesPane = new JTabbedPane();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel2.add(categoriesPane, gbc);
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    private String $$$getMessageFromBundle$$$(String path, String key) {
        ResourceBundle bundle;
        try {
            Class<?> thisClass = this.getClass();
            if ($$$cachedGetBundleMethod$$$ == null) {
                Class<?> dynamicBundleClass = thisClass.getClassLoader().loadClass("com.intellij.DynamicBundle");
                $$$cachedGetBundleMethod$$$ = dynamicBundleClass.getMethod("getBundle", String.class, Class.class);
            }
            bundle = (ResourceBundle) $$$cachedGetBundleMethod$$$.invoke(null, path, thisClass);
        } catch (Exception e) {
            bundle = ResourceBundle.getBundle(path);
        }
        return bundle.getString(key);
    }

    /**
     * @noinspection ALL
     */
    private void $$$loadButtonText$$$(AbstractButton component, String text) {
        StringBuffer result = new StringBuffer();
        boolean haveMnemonic = false;
        char mnemonic = '\0';
        int mnemonicIndex = -1;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '&') {
                i++;
                if (i == text.length()) break;
                if (!haveMnemonic && text.charAt(i) != '&') {
                    haveMnemonic = true;
                    mnemonic = text.charAt(i);
                    mnemonicIndex = result.length();
                }
            }
            result.append(text.charAt(i));
        }
        component.setText(result.toString());
        if (haveMnemonic) {
            component.setMnemonic(mnemonic);
            component.setDisplayedMnemonicIndex(mnemonicIndex);
        }
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }

}
