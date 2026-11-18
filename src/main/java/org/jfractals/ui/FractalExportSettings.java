package org.jfractals.ui;

import org.jfractals.utils.MathUtils;

public final class FractalExportSettings {
    private int widthPx = 1920;
    private int heightPx = 1080;
    private int aspectRatioWidth = 16;
    private int aspectRatioHeight = 9;
    private FractalExportImageExtension imageExtension = FractalExportImageExtension.PNG;

    public int getWidthPx() {
        return widthPx;
    }
    public void setWidthPx(int newWidthPx) {
        widthPx = newWidthPx;
        int gcd = MathUtils.greatestCommonDivisor(widthPx, heightPx);
        aspectRatioWidth = widthPx / gcd;
        aspectRatioHeight = heightPx / gcd;
    }

    public int getHeightPx() {
        return heightPx;
    }
    public void setHeightPx(int newHeightPx) {
        heightPx = newHeightPx;
        int gcd = MathUtils.greatestCommonDivisor(widthPx, heightPx);
        aspectRatioWidth = widthPx / gcd;
        aspectRatioHeight = heightPx / gcd;
    }

    public int getAspectRatioWidth() {
        return aspectRatioWidth;
    }
    public void setAspectRatioWidth(int newAspectRatioWidth) {
        aspectRatioWidth = newAspectRatioWidth;
        setWidthPx(aspectRatioWidth * heightPx / aspectRatioHeight);
    }

    public int getAspectRatioHeight() {
        return aspectRatioHeight;
    }
    public void setAspectRatioHeight(int newAspectRatioHeight) {
        aspectRatioHeight = newAspectRatioHeight;
        setHeightPx(widthPx * aspectRatioHeight / aspectRatioWidth);
    }

    public FractalExportImageExtension getImageExtension() { return imageExtension; }
    public void setImageExtension(FractalExportImageExtension imageExtension) { this.imageExtension = imageExtension; }
}
