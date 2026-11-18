package org.jfractals.ui;

public enum FractalExportImageExtension {
    PNG("png"),
    JPEG("jpeg");

    private final String displayName;

    FractalExportImageExtension(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}