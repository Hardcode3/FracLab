package org.fraclab.export;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

/**
 * Image file formats a fractal can be exported to.
 * Each one must have a writer in {@link javax.imageio.ImageIO}, see
 * <a href="https://docs.oracle.com/en/java/javase/25/docs/api/java.desktop/javax/imageio/package-summary.html">javax.imageio</a>.
 */
public enum ImageFormat {
    GIF("gif"),
    PNG("png"),
    TIFF("tiff");

    private final String name;

    ImageFormat(String name) {
        this.name = name;
    }

    /** The file extension, without a dot, which is also the ImageIO writer format name. */
    public String extension() {
        return name;
    }

    /** A file chooser pattern matching this format, such as {@code *.png}. */
    public String filePattern() {
        return "*." + name;
    }

    /** File chooser patterns of all formats. */
    public static String[] allFilePatterns() {
        return Arrays.stream(values()).map(ImageFormat::filePattern).toArray(String[]::new);
    }

    /** The format whose extension ends the given file name, ignoring case. */
    public static Optional<ImageFormat> fromFileName(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot < 0) {
            return Optional.empty();
        }
        String extension = fileName.substring(lastDot + 1).toLowerCase(Locale.ROOT);
        return Arrays.stream(values()).filter(format -> format.name.equals(extension)).findFirst();
    }

    /** The same file with its extension replaced by (or, if it has none, completed with) this format's. */
    public Path applyTo(Path file) {
        Path fileName = file.getFileName();
        if (fileName == null) {
            return file;
        }
        String name = fileName.toString();
        int lastDot = name.lastIndexOf('.');
        String baseName = lastDot > 0 ? name.substring(0, lastDot) : name;
        return file.resolveSibling(baseName + "." + this.name);
    }

    @Override
    public String toString() {
        return name;
    }
}
