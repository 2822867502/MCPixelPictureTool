package org.zlk.mcpixelpicturetool.form.dialog;

import javax.swing.*;
import java.awt.event.ActionListener;

public class IconOKDialog extends OKDialog {

    public IconOKDialog(String title, Icon icon, String content, JFrame owner, ActionListener okListener) {
        super(title, content, owner, okListener);
        contentText.setIcon(icon);
    }

    public IconOKDialog(String title, Icon icon, String content, JFrame owner) {
        super(title, content, owner);
        contentText.setIcon(icon);
    }
}
