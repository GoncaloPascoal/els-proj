package pt.up.fe.els2022.utils;

import java.io.File;

public final class FileUtils {
    private FileUtils() {}

    public static String getExtension(File file) {
        return getExtension(file.getName());
    }

    public static String getExtension(String name) {
        int index = name.lastIndexOf('.');
        if (index > 0) return name.substring(index + 1);
        return "";
    }
}
