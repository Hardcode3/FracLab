package org.fraclab.ui.export;

import java.util.Arrays;

public enum FractalExportImageExtension {
    // Formats should be supported by imageio
    // cf. https://docs.oracle.com/javase/8/docs/api/javax/imageio/package-summary.html
    GIF("gif"),
    PNG("png"),
    TIFF("tiff");

    private final String displayName;

    FractalExportImageExtension(String displayName) {
        this.displayName = displayName;
    }

    public String withDot() {
        return "." + displayName;
    }

    public String withWildcard() {
        return "*." + displayName;
    }

    public static String[] getAllPatterns() {
        return Arrays.stream(values())
                .map(FractalExportImageExtension::withWildcard)
                .toArray(String[]::new);
    }

    @Override
    public String toString() {
        return displayName;
    }
}