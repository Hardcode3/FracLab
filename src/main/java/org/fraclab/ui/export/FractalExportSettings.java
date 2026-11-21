package org.fraclab.ui.export;

import org.fraclab.utils.MathUtils;

import java.awt.GraphicsEnvironment;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class FractalExportSettings {
    private final int DEFAULT_WIDTH_PX = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode().getWidth();
    private final int DEFAULT_HEIGHT_PX = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode().getHeight();
    private final int DEFAULT_GCD = MathUtils.greatestCommonDivisor(DEFAULT_WIDTH_PX, DEFAULT_HEIGHT_PX);

    private final static FractalExportImageExtension DEFAULT_IMG_EXTENSION = FractalExportImageExtension.PNG;
    private final static File DEFAULT_SAVE_LOCATION = new File(System.getProperty("user.home"), "Downloads");
    private final static String DEFAULT_FILE_NAME_DATETIME_FORMAT = "yyyy_MM_dd-HH_mm_ss";
    private final static String DEFAULT_FILE_NAME_PREFIX = "fractal_image";
    private final static File DEFAULT_FILE_PATH = new File(DEFAULT_SAVE_LOCATION, buildDefaultFName());

    private int widthPx = DEFAULT_WIDTH_PX;
    private int heightPx = DEFAULT_HEIGHT_PX;
    private int aspectRatioWidth = DEFAULT_WIDTH_PX / DEFAULT_GCD;
    private int aspectRatioHeight = DEFAULT_HEIGHT_PX / DEFAULT_GCD;
    private FractalExportImageExtension imageExtension = DEFAULT_IMG_EXTENSION;
    private File fpath = DEFAULT_FILE_PATH;

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

    public File getFpath() {
        return fpath;
    }
    public String getFpathString() {
        return fpath.getAbsolutePath();
    }
    public void setFPath(File fpath) {
        this.fpath = fpath;
    }
    public void setFPath(String fpath) {
        this.fpath = new File(fpath);
    }
    public boolean isFPathValid() {
        return (fpath != null &&
                fpath.getParentFile() != null &&
                fpath.getParentFile().canRead() &&
                fpath.getParentFile().exists()
        );
    }

    private static String buildDefaultFName() {
        String currentDateTime = new SimpleDateFormat(DEFAULT_FILE_NAME_DATETIME_FORMAT).format(new Date());
        return DEFAULT_FILE_NAME_PREFIX + "_" + currentDateTime + DEFAULT_IMG_EXTENSION.withDot();
    }
}
