package org.zlk.mcpixelpicturetool.utils;

import com.github.weisj.darklaf.LafManager;
import com.github.weisj.darklaf.theme.Theme;
import org.zlk.mcpixelpicturetool.form.dialog.IconOKDialog;
import org.zlk.mcpixelpicturetool.form.dialog.OKDialog;
import org.zlk.mcpixelpicturetool.form.dialog.UseBlocksChooser;
import org.zlk.mcpixelpicturetool.type.Block;
import org.zlk.mcpixelpicturetool.type.property.KeyValueI18NProperty;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.Set;


public class GUIUtils {

    public static void okDialog(String title, String content, JFrame owner) {
        OKDialog dialog = new OKDialog(title, content, owner);
        dialog.pack();
        dialog.setMinimumSize(dialog.getSize());
        dialog.setVisible(true);
    }

    public static void iconOKDialog(String title, Icon icon, String content, JFrame owner) {
        IconOKDialog dialog = new IconOKDialog(title, icon, content, owner);
        dialog.pack();
        dialog.setMinimumSize(dialog.getSize());
        dialog.setVisible(true);
    }

    private static void iconOKDialogByKey(String title, String content, String iconKey, JFrame owner) {
        try {
            iconOKDialog(title, UIManager.getIcon(iconKey), content, owner);
        } catch (NullPointerException e) {
            okDialog(I18NUtils.getGUIString("gui.utils.error.dialog"), content, owner);
            //在获取错误图标时，存在空指针风险
        }
    }

    public static void simpleInfoDialog(String content, JFrame owner) {
        iconOKDialogByKey(I18NUtils.getGUIString("gui.utils.info.dialog"), content, "OptionPane.infoIcon", owner);
    }

    public static void simpleWarnDialog(String content, JFrame owner) {
        iconOKDialogByKey(I18NUtils.getGUIString("gui.utils.warn.dialog"), content, "OptionPane.warningIcon", owner);
    }

    public static void simpleErrorDialog(String content, JFrame owner) {
        iconOKDialogByKey(I18NUtils.getGUIString("gui.utils.error.dialog"), content, "OptionPane.errorIcon", owner);
    }

    public static JFileChooser fileChooserBuild(String dialogTitle, File currentDictory, int dialogType, int selectMode, FileFilter fileFilter) {
        JFileChooser chooser = new JFileChooser(currentDictory);
        chooser.setDialogType(dialogType);
        chooser.setFileSelectionMode(selectMode);
        chooser.setDialogTitle(dialogTitle);
        chooser.setFileFilter(fileFilter);
        return chooser;
    }

    public static UseBlocksChooser useBlocksChooserBuild(String dialogTitle, Set<Block> selectedBlocks, JFrame owner) {
        UseBlocksChooser chooser = new UseBlocksChooser(owner, selectedBlocks);
        chooser.setTitle(dialogTitle);
        return chooser;
    }

    public static JMenu createHelpMenu() {
        JMenu menu = new JMenu(I18NUtils.getGUIString("gui.menu.help"));
        JMenuItem about = new JMenuItem(I18NUtils.getGUIString("gui.menu.about"));
        about.addActionListener(evt -> {
            String file = I18NUtils.getGUIString("gui.menu.about.content");
            BufferedReader br = new BufferedReader(new InputStreamReader(I18NUtils.getResources(file), StandardCharsets.UTF_8));
            StringBuilder content = new StringBuilder();
            br.lines().forEach((s) -> {
                content.append(s);
                content.append("\n");
            });
            try {
                BufferedImage icon = ImageUtils.fromStream(I18NUtils.getResources("icon.png"));
                icon = ImageUtils.resize(
                        icon,
                        Integer.parseInt(I18NUtils.getGUIString("gui.default.about.icon.width")),
                        Integer.parseInt(I18NUtils.getGUIString("gui.default.about.icon.height")));
                iconOKDialog(
                        I18NUtils.getGUIString("gui.menu.about"),
                        new ImageIcon(icon),
                        content.toString(),
                        null);
            } catch (IOException e) {
                throw new RuntimeException(e);
                //当出现这种问题，责任在代码，因此不必处理，直接抛
            }
            menu.setSelected(false);
        });
        menu.add(about);
        return menu;
    }

    public static JMenu createThemeMenu() {
        JMenu menu = new JMenu(I18NUtils.getGUIString("gui.menu.theme"));
        ButtonGroup buttonGroup = new ButtonGroup();
        KeyValueI18NProperty[] themes = I18NUtils.getDatas("data.themes");
        KeyValueI18NProperty defaultTheme = I18NUtils.getData("default.theme");
        for (KeyValueI18NProperty t : themes) {
            JRadioButtonMenuItem item = createThemeMenuItem(t, defaultTheme.equals(t));
            menu.add(item);
            buttonGroup.add(item);
        }
        menu.revalidate();
        menu.repaint();
        return menu;
    }

    private static JRadioButtonMenuItem createThemeMenuItem(KeyValueI18NProperty t, boolean isSelected) {
        JRadioButtonMenuItem item = new JRadioButtonMenuItem(t.i18N);
        item.addActionListener((e) -> setThemeWithProperty(t));
        item.setSelected(isSelected);
        return item;
    }

    //他不能在创建Item时调用，不然主题设置不完全
    public static void setThemeWithProperty(KeyValueI18NProperty t) {
        try {
            Class<?> themeClazz = Class.forName(t.value);
            LafManager.install((Theme) themeClazz.getConstructor().newInstance());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                 InvocationTargetException | NoSuchMethodException | ClassCastException ex) {
            throw new RuntimeException(ex);
            //当出现这种问题，责任在代码，因此不必处理，直接抛
        }
    }
}
