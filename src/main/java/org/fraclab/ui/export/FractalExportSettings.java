package org.fraclab.ui.export;

import org.fraclab.export.ImageFormat;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * State of the export form: image size and aspect ratio, format and destination file.
 * <p>
 * The size and the aspect ratio stay consistent: changing the size recomputes the reduced ratio, and
 * changing one term of the ratio resizes the matching side of the image. Sizes are clamped to
 * [{@link #MIN_SIZE_PX}, {@link #MAX_SIZE_PX}].
 */
final class FractalExportSettings {
    static final int MIN_SIZE_PX = 100;
    static final int MAX_SIZE_PX = 999_999;
    static final int MIN_RATIO_TERM = 1;
    static final int MAX_RATIO_TERM = MAX_SIZE_PX;

    private static final ImageFormat DEFAULT_FORMAT = ImageFormat.PNG;
    private static final String DEFAULT_FILE_NAME_PREFIX = "fractal_image_";
    private static final DateTimeFormatter DEFAULT_FILE_NAME_DATE_TIME = DateTimeFormatter.ofPattern("yyyy_MM_dd-HH_mm_ss");

    private int widthPx;
    private int heightPx;
    private int aspectRatioWidth;
    private int aspectRatioHeight;
    private ImageFormat format = DEFAULT_FORMAT;
    private Path outputFile;

    /**
     * @param widthPx    Initial image width.
     * @param heightPx   Initial image height.
     * @param outputFile Initial destination file.
     */
    FractalExportSettings(int widthPx, int heightPx, Path outputFile) {
        this.widthPx = clampSize(widthPx);
        this.heightPx = clampSize(heightPx);
        this.outputFile = outputFile;
        updateAspectRatio();
    }

    /**
     * A timestamped file name in the user's Downloads folder (or home folder if there is none),
     * so that successive exports do not overwrite each other.
     */
    static Path defaultOutputFile() {
        Path home = Path.of(System.getProperty("user.home"));
        Path downloads = home.resolve("Downloads");
        Path folder = Files.isDirectory(downloads) ? downloads : home;
        String fileName = DEFAULT_FILE_NAME_PREFIX + LocalDateTime.now().format(DEFAULT_FILE_NAME_DATE_TIME);
        return DEFAULT_FORMAT.applyTo(folder.resolve(fileName));
    }

    int getWidthPx() {
        return widthPx;
    }

    void setWidthPx(int newWidthPx) {
        widthPx = clampSize(newWidthPx);
        updateAspectRatio();
    }

    int getHeightPx() {
        return heightPx;
    }

    void setHeightPx(int newHeightPx) {
        heightPx = clampSize(newHeightPx);
        updateAspectRatio();
    }

    int getAspectRatioWidth() {
        return aspectRatioWidth;
    }

    /** Resizes the width so that width / height matches the new ratio. */
    void setAspectRatioWidth(int newAspectRatioWidth) {
        long newWidth = (long) Math.max(newAspectRatioWidth, MIN_RATIO_TERM) * heightPx / aspectRatioHeight;
        setWidthPx(clampSize(newWidth));
    }

    int getAspectRatioHeight() {
        return aspectRatioHeight;
    }

    /** Resizes the height so that width / height matches the new ratio. */
    void setAspectRatioHeight(int newAspectRatioHeight) {
        long newHeight = (long) widthPx * Math.max(newAspectRatioHeight, MIN_RATIO_TERM) / aspectRatioWidth;
        setHeightPx(clampSize(newHeight));
    }

    ImageFormat getFormat() {
        return format;
    }

    void setFormat(ImageFormat format) {
        this.format = Objects.requireNonNull(format, "format");
    }

    /** The destination file, or {@code null} if none or an invalid one was entered. */
    Path getOutputFile() {
        return outputFile;
    }

    void setOutputFile(Path outputFile) {
        this.outputFile = outputFile;
    }

    /** Whether the destination is a file name in an existing folder that can be written to. */
    boolean isOutputFileValid() {
        if (outputFile == null || outputFile.getFileName() == null || Files.isDirectory(outputFile)) {
            return false;
        }
        Path folder = outputFile.toAbsolutePath().getParent();
        return folder != null && Files.isDirectory(folder) && Files.isWritable(folder);
    }

    private void updateAspectRatio() {
        int gcd = greatestCommonDivisor(widthPx, heightPx);
        aspectRatioWidth = widthPx / gcd;
        aspectRatioHeight = heightPx / gcd;
    }

    private static int clampSize(long size) {
        return Math.clamp(size, MIN_SIZE_PX, MAX_SIZE_PX);
    }

    private static int greatestCommonDivisor(int a, int b) {
        while (b != 0) {
            int remainder = a % b;
            a = b;
            b = remainder;
        }
        return Math.abs(a);
    }
}
