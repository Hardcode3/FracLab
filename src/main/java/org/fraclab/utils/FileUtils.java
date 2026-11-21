package org.fraclab.utils;

public class FileUtils {
    public static String replaceFileExtension(String filePath, String newExtension) {
        if (!newExtension.startsWith(".")) {
            newExtension = "." + newExtension;
        }
        int lastDot = filePath.lastIndexOf(".");
        int lastSlash = Math.max(filePath.lastIndexOf("/"), filePath.lastIndexOf("\\"));

        if (lastDot > lastSlash) {
            return filePath.substring(0, lastDot) + newExtension;
        } else {
            return filePath + newExtension;
        }
    }
}
