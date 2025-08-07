package org.zlk.mcpixelpicturetool.type;

import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.util.Arrays;
import java.util.HashSet;


public class SuffixFileFilter extends FileFilter {
    private final HashSet<String> supportedSuffix;
    private final boolean showDirectories;

    public SuffixFileFilter(boolean showDirectories, HashSet<String> supportedSuffix) {
        this.showDirectories = showDirectories;
        this.supportedSuffix = supportedSuffix;
    }

    public SuffixFileFilter(boolean showDirectories, String... supportedSuffix) {
        this(showDirectories, new HashSet<>(Arrays.asList(supportedSuffix)));
    }

    public SuffixFileFilter(String... supportedSuffix) {
        this(true, supportedSuffix);
    }

    public static String getSuffix(File file) {
        String filename = file.getName();
        String[] splitFilename = filename.split("\\.");
        if (splitFilename.length < 1) return null;
        return splitFilename.length == 1 ? "" : splitFilename[splitFilename.length - 1];
    }

    @Override
    public boolean accept(File pathname) {
        if (pathname.isDirectory()) return showDirectories;
        String suffix = getSuffix(pathname);
        return supportedSuffix.contains(suffix);
    }

    @Override
    public String getDescription() {
        StringBuilder buffer = new StringBuilder();
        for (String s : supportedSuffix) {
            if (s == null || s.isEmpty()) continue;
            buffer.append('.');
            buffer.append(s);
            buffer.append('/');
        }
        buffer.deleteCharAt(buffer.length() - 1);
        return buffer.toString();
    }

}
