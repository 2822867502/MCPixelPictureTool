package org.zlk.mcpixelpicturetool.form;

import org.zlk.mcpixelpicturetool.form.dialog.UseBlocksChooser;
import org.zlk.mcpixelpicturetool.maker.base.type.UseThreadCalculator;
import org.zlk.mcpixelpicturetool.maker.generator.picture.PixelPictureGenerator;
import org.zlk.mcpixelpicturetool.maker.processor.picture.GeneratePixelPictureProcessor;
import org.zlk.mcpixelpicturetool.type.Block;
import org.zlk.mcpixelpicturetool.type.BlocksFormatter;
import org.zlk.mcpixelpicturetool.type.SuffixFileFilter;
import org.zlk.mcpixelpicturetool.type.property.KeyValueI18NProperty;
import org.zlk.mcpixelpicturetool.utils.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.ResourceBundle;

public class MainFrame {
    private JFrame mainFrame;
    private JPanel mainPanel;
    private JTextField filePathText;
    private JButton fileOpenButton;
    private JLabel originImageLabel;
    private JLabel arrow;
    private JLabel previewImageLabel;
    private JButton saveButton;
    private JButton makeButton;
    private JFormattedTextField targetWidthInput;
    private JFormattedTextField targetHeightInput;
    private JFormattedTextField useBlocksInput;
    private JButton chooseUseBlocksButton;
    private JComboBox<String> modeInput;
    private JPanel imagePanel;
    private File file;
    private BufferedImage originImage;
    private BufferedImage previewImage;
    private final PixelPictureGenerator generator = new PixelPictureGenerator(ProcessorUtils.PROCESSOR_MANAGER);


    public MainFrame(JFrame mainFrame) {
        this.mainFrame = mainFrame;
        try {
            mainFrame.setIconImage(ImageUtils.fromStream(I18NUtils.getResources("icon.png")));
        } catch (IOException e) {
            throw new RuntimeException(e);
            //当出现这种问题，责任在代码，因此不必处理，直接抛
        }

        fileOpenButton.addActionListener(this::chooseFile);
        mainPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                resizeImage(e);
            }
        });
        makeButton.addActionListener(this::make);
        saveButton.addActionListener(this::save);
        chooseUseBlocksButton.addActionListener(this::chooseChooseUseBlocks);

        try {
            NumberFormatter numberFormatter = new NumberFormatter(NumberFormat.getIntegerInstance());
            numberFormatter.setMinimum(0);
            numberFormatter.setMaximum(Integer.MAX_VALUE);

            targetHeightInput.addPropertyChangeListener("value", (e) -> {
                generator.setTargetHeight((Integer) targetHeightInput.getValue());
                updateMakeButton();
            });
            targetHeightInput.setFormatterFactory(new DefaultFormatterFactory(numberFormatter));
            int defaultHeight = (int) numberFormatter.stringToValue(I18NUtils.getDataString("default.targetHeight"));
            targetHeightInput.setValue(defaultHeight);

            targetWidthInput.addPropertyChangeListener("value", (e) -> {
                generator.setTargetWidth((Integer) targetWidthInput.getValue());
                updateMakeButton();
            });
            targetWidthInput.setFormatterFactory(new DefaultFormatterFactory(numberFormatter));
            int defaultWidth = (int) numberFormatter.stringToValue(I18NUtils.getDataString("default.targetWidth"));
            targetWidthInput.setValue(defaultWidth);

            useBlocksInput.addPropertyChangeListener("value", (e) -> {
                generator.setChoiceBlocks((Block[]) useBlocksInput.getValue());
                updateMakeButton();
            });
            BlocksFormatter blocksFormatter = new BlocksFormatter();
            useBlocksInput.setFormatterFactory(new DefaultFormatterFactory(blocksFormatter));
            Block[] defaultBlocks = (Block[]) blocksFormatter.stringToValue(I18NUtils.getDataString("default.useBlocks"));
            useBlocksInput.setValue(defaultBlocks);

        } catch (ParseException e) {
            GUIUtils.simpleErrorDialog(I18NUtils.getGUIString("gui.main.error.unexpected_error"), mainFrame);
            throw new RuntimeException(e);
        }

        modeInput.addActionListener((e) -> {
            generator.setMode(modeInput.getSelectedIndex());
        });
        KeyValueI18NProperty[] modes = I18NUtils.getDatas("data.modes");
        String defaultModeKey = I18NUtils.getDataString("default.key.mode");
        //这里实际上上有点麻烦，但是如果不这么写
        //要么实现I18N的缓存，但是那样点费事
        //要么后面改改，但是那样也费事
        for (int i = 0; i < modes.length; ++i) {
            KeyValueI18NProperty m = modes[i];
            int v = Integer.parseInt(m.value);
            modeInput.addItem(m.i18N);
            if (defaultModeKey.equals(m.key)) modeInput.setSelectedIndex(v);
            if (v != i) throw new RuntimeException("Please write the data in order");//这里的检查是为了防止后面的取值时出错
        }

        JMenuBar menu = new JMenuBar();
        menu.add(GUIUtils.createThemeMenu());
        menu.add(GUIUtils.createHelpMenu());
        mainFrame.setJMenuBar(menu);

        //warn 仅供测试
        generator.setTransparentStrategy(GeneratePixelPictureProcessor.STRATEGY_NOTHING);
    }

    private static void setImageSize(double imageSize, BufferedImage image, JLabel imageLabel) {
        if (image != null) {
            double rate = imageSize / Math.max(image.getWidth(imageLabel), image.getHeight(imageLabel));
            BufferedImage newImage = ImageUtils.resize(image, rate);
            imageLabel.setIcon(new ImageIcon(newImage));
            imageLabel.revalidate();
            imageLabel.repaint();
        }
    }

    private void setInitSize() {
        int width = Integer.parseInt(I18NUtils.getGUIString("gui.default.image.width"));
        int height = Integer.parseInt(I18NUtils.getGUIString("gui.default.image.height"));
        Dimension imageSizeDimension = new Dimension(width, height);
        originImageLabel.setMinimumSize(imageSizeDimension);
        originImageLabel.setPreferredSize(imageSizeDimension);
        previewImageLabel.setMinimumSize(imageSizeDimension);
        previewImageLabel.setPreferredSize(imageSizeDimension);
    }

    private void chooseFile(ActionEvent evt) {
        FileFilter filter = new SuffixFileFilter(ImageIO.getReaderFileSuffixes());
        //todo 在PropertyChangeSupport的firePropertyChange的倒数第二个fire占用太长时间导致反应慢
        //经检测，不是我filter的问题
        JFileChooser chooser =
                GUIUtils.fileChooserBuild(
                        I18NUtils.getGUIString("gui.main.file.open.filechooser.title"),
                        new File(System.getProperty("user.dir")),
                        JFileChooser.OPEN_DIALOG,
                        JFileChooser.FILES_ONLY,
                        filter);
        SwingUtilities.invokeLater(() -> {
            int result = 0;

            result = chooser.showDialog(this.mainFrame, I18NUtils.getGUIString("gui.main.open.file.filechooser.button"));

            switch (result) {
                case JFileChooser.APPROVE_OPTION: {
                    file = chooser.getSelectedFile();
                    if (!filter.accept(file)) {
                        GUIUtils.simpleWarnDialog(I18NUtils.getGUIString("gui.main.warn.unsupported_file", filter.getDescription()), mainFrame);
                        file = null;
                        return;
                    }
                    filePathText.setText(file.getAbsolutePath());
                    try {
                        generator.setAndLoadOriginImageFile(file);
                        originImage = generator.getOriginImageOrLoad();
                        //warn 仅供测试
                    } catch (IOException e) {
                        GUIUtils.simpleErrorDialog(I18NUtils.getGUIString("gui.main.image.origin.cannot_load_image", file.toString()), mainFrame);
                    }

                    int processors = Runtime.getRuntime().availableProcessors();
                    generator.setCastChoiceBlockToLabUseThreadCalculator(new UseThreadCalculator(processors, UseThreadCalculator.SET_USE_THREAD));
                    generator.setGeneratePixelPictureUseThreadCalculator(new UseThreadCalculator(processors, UseThreadCalculator.SET_USE_THREAD));
                    //warn 仅供测试
                    previewImage = null;
                    resizeImage(null);
                    updateSaveButton();
                    updateMakeButton();
                }
                break;
                case JFileChooser.CANCEL_OPTION: {
                    //do nothing
                }
                break;
                default: {
                    GUIUtils.simpleErrorDialog(I18NUtils.getGUIString("gui.main.error.choose_image_file"), mainFrame);
                }
            }
        });
    }

    private void resizeImage(ComponentEvent evt) {
        SwingUtilities.invokeLater(() -> {
            int imageSize = imagePanel.getHeight();
            Dimension labelSize = new Dimension(imageSize, imageSize);
            originImageLabel.setPreferredSize(labelSize);
            previewImageLabel.setPreferredSize(labelSize);

            setImageSize(imageSize, originImage, originImageLabel);
            setImageSize(imageSize, previewImage, previewImageLabel);
            //todo 很神奇的bug，在非原始窗口大小的情况下，调用该方法图片大小会变
        });
    }

    private void make(ActionEvent evt) {
        SwingUtilities.invokeLater(() -> {
            int targetWidth = (Integer) targetWidthInput.getValue();
            int targetHeight = (Integer) targetHeightInput.getValue();
            Block[] blocks = (Block[]) useBlocksInput.getValue();
            if (blocks == null || blocks.length == 0) {
                GUIUtils.simpleWarnDialog(I18NUtils.getGUIString("gui.main.make.warn.please_choose_use_blocks"), mainFrame);
                return;
            }
            generator.clearAll();
            //warn 乱七八糟的clear方法，真不能用
            try {
                generator.setAndLoadOriginImageFile(file);
            } catch (IOException e) {
                GUIUtils.simpleErrorDialog(I18NUtils.getGUIString("gui.main.image.origin.cannot_load_image", file.toString()), mainFrame);
            }
            //warn 这不是好代码，仅供临时使用测试
            generator.generate();
            previewImage = generator.getPreviewImageOrExport();//warn 仅供测试
            resizeImage(null);
            updateSaveButton();
            GUIUtils.simpleInfoDialog(I18NUtils.getGUIString("gui.main.make.success"), mainFrame);
        });
    }

    private void updateSaveButton() {
        saveButton.setEnabled(generator.isGenerated());
    }

    private void updateMakeButton() {
        makeButton.setEnabled(generator.canGenerate());
    }

    private void save(ActionEvent evt) {
        FileFilter filter = new SuffixFileFilter("", "nbt");
        JFileChooser chooser =
                GUIUtils.fileChooserBuild(
                        I18NUtils.getGUIString("gui.main.save.filechooser.title"),
                        new File(System.getProperty("user.dir")),
                        JFileChooser.SAVE_DIALOG,
                        JFileChooser.FILES_ONLY,
                        filter);
        //这里不能异步执行，不然有maker == null的风险
        int result = chooser.showDialog(this.mainFrame, I18NUtils.getGUIString("gui.main.save.filechooser.button"));
        switch (result) {
            case JFileChooser.APPROVE_OPTION: {
                File saveFile = chooser.getSelectedFile();
                if ("".equals(SuffixFileFilter.getSuffix(saveFile)))
                    saveFile = new File(saveFile.getParent(), saveFile.getName() + ".nbt");
                if (!filter.accept(saveFile)) {
                    GUIUtils.simpleWarnDialog(I18NUtils.getGUIString("gui.main.warn.unsupported_file", filter.getDescription()), mainFrame);
                    return;
                }
                try {
                    NBTUtils.write(
                            generator.getNBT(),
                            saveFile
                    );
                } catch (IOException e) {
                    GUIUtils.simpleErrorDialog(I18NUtils.getGUIString("gui.main.save.cannot_write_to_file", file.toString()), mainFrame);
                }
                if (saveFile.exists())
                    GUIUtils.simpleInfoDialog(I18NUtils.getGUIString("gui.main.save.success"), mainFrame);
            }
            break;
            case JFileChooser.CANCEL_OPTION: {
                //do nothing
            }
            break;
            default: {
                GUIUtils.simpleErrorDialog(I18NUtils.getGUIString("gui.main.error.choose_image_file"), mainFrame);
            }
        }

    }

    private void chooseChooseUseBlocks(ActionEvent evt) {
        UseBlocksChooser chooser = GUIUtils.useBlocksChooserBuild(
                I18NUtils.getGUIString("gui.main.useblockschooser.title"),
                new HashSet<>(Arrays.asList((Block[]) useBlocksInput.getValue())),
                mainFrame
        );
        SwingUtilities.invokeLater(() -> {
            int result = chooser.showDialog();
            switch (result) {
                case UseBlocksChooser.APPROVE_OPTION: {
                    Block[] selectedBlocks = chooser.getSelectedBlocks();
                    useBlocksInput.setValue(selectedBlocks);
                }
                break;
                case UseBlocksChooser.CANCEL_OPTION: {
                    //do nothing
                }
                break;
                default: {
                    GUIUtils.simpleErrorDialog(I18NUtils.getGUIString("gui.main.useblockschooser.error.choose_use_blocks"), mainFrame);
                }
            }
        });
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridBagLayout());
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(panel1, gbc);
        filePathText = new JTextField();
        filePathText.setEditable(false);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 15.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(filePathText, gbc);
        fileOpenButton = new JButton();
        fileOpenButton.setMargin(new Insets(0, 0, 0, 0));
        this.$$$loadButtonText$$$(fileOpenButton, this.$$$getMessageFromBundle$$$("res", "gui.main.file.open.button"));
        fileOpenButton.setVerticalTextPosition(0);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(fileOpenButton, gbc);
        imagePanel = new JPanel();
        imagePanel.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(imagePanel, gbc);
        arrow = new JLabel();
        arrow.setHorizontalAlignment(0);
        this.$$$loadLabelText$$$(arrow, this.$$$getMessageFromBundle$$$("res", "gui.main.arrow.right"));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.VERTICAL;
        imagePanel.add(arrow, gbc);
        previewImageLabel = new JLabel();
        previewImageLabel.setHorizontalAlignment(0);
        previewImageLabel.setHorizontalTextPosition(0);
        previewImageLabel.setPreferredSize(new Dimension(128, 128));
        previewImageLabel.setText("");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 2.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        imagePanel.add(previewImageLabel, gbc);
        originImageLabel = new JLabel();
        originImageLabel.setHorizontalAlignment(0);
        originImageLabel.setHorizontalTextPosition(0);
        originImageLabel.setPreferredSize(new Dimension(128, 128));
        originImageLabel.setText("");
        originImageLabel.setVerticalAlignment(0);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 2.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        imagePanel.add(originImageLabel, gbc);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(panel2, gbc);
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel2.add(panel3, gbc);
        final JLabel label1 = new JLabel();
        this.$$$loadLabelText$$$(label1, this.$$$getMessageFromBundle$$$("res", "gui.main.target.label.height"));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        panel3.add(label1, gbc);
        targetHeightInput = new JFormattedTextField();
        targetHeightInput.setText(this.$$$getMessageFromBundle$$$("data", "default.targetHeight"));
        targetHeightInput.setToolTipText("");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(targetHeightInput, gbc);
        final JLabel label2 = new JLabel();
        this.$$$loadLabelText$$$(label2, this.$$$getMessageFromBundle$$$("res", "gui.main.target.label.width"));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        panel3.add(label2, gbc);
        targetWidthInput = new JFormattedTextField();
        targetWidthInput.setText(this.$$$getMessageFromBundle$$$("data", "default.targetWidth"));
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(targetWidthInput, gbc);
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel2.add(panel4, gbc);
        final JLabel label3 = new JLabel();
        this.$$$loadLabelText$$$(label3, this.$$$getMessageFromBundle$$$("res", "gui.main.target.label.blocks"));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        panel4.add(label3, gbc);
        chooseUseBlocksButton = new JButton();
        this.$$$loadButtonText$$$(chooseUseBlocksButton, this.$$$getMessageFromBundle$$$("res", "gui.main.target.blocks.button"));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel4.add(chooseUseBlocksButton, gbc);
        useBlocksInput = new JFormattedTextField();
        useBlocksInput.setText(this.$$$getMessageFromBundle$$$("data", "default.useBlocks"));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel4.add(useBlocksInput, gbc);
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel2.add(panel5, gbc);
        final JLabel label4 = new JLabel();
        this.$$$loadLabelText$$$(label4, this.$$$getMessageFromBundle$$$("res", "gui.main.target.mode.label"));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        panel5.add(label4, gbc);
        modeInput = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        modeInput.setModel(defaultComboBoxModel1);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel5.add(modeInput, gbc);
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.VERTICAL;
        mainPanel.add(panel6, gbc);
        saveButton = new JButton();
        saveButton.setEnabled(false);
        this.$$$loadButtonText$$$(saveButton, this.$$$getMessageFromBundle$$$("res", "gui.main.save.button"));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel6.add(saveButton, gbc);
        makeButton = new JButton();
        makeButton.setEnabled(false);
        this.$$$loadButtonText$$$(makeButton, this.$$$getMessageFromBundle$$$("res", "gui.main.make.button"));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel6.add(makeButton, gbc);
    }

    private static Method $$$cachedGetBundleMethod$$$ = null;

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
    private void $$$loadLabelText$$$(JLabel component, String text) {
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
            component.setDisplayedMnemonic(mnemonic);
            component.setDisplayedMnemonicIndex(mnemonicIndex);
        }
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
        return mainPanel;
    }

    public static void main(String[] args) {
        GUIUtils.setThemeWithProperty(I18NUtils.getData("default.theme"));
        MainFrame mainFrame = new MainFrame(new JFrame(I18NUtils.getGUIString("gui.main.frame.title")));
        mainFrame.mainFrame.setContentPane(mainFrame.mainPanel);
        mainFrame.mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setInitSize();
        mainFrame.mainFrame.pack();
        mainFrame.mainFrame.setMinimumSize(mainFrame.mainFrame.getPreferredSize());
        mainFrame.mainFrame.setLocationRelativeTo(null);
        mainFrame.mainFrame.setVisible(true);
    }

}
