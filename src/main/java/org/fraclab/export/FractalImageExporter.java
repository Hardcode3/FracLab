package org.fraclab.export;

import org.fraclab.render.FractalRenderer;
import org.fraclab.render.RenderRequest;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.DirectColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * Renders fractal images and writes them to files, without depending on JavaFX.
 * Each export runs on its own background thread, so the caller is never blocked.
 */
public final class FractalImageExporter {
    private static final Executor EXPORT_THREADS =
            task -> Thread.ofPlatform().name("fractal-export").daemon().start(task);

    /** Reads the RGB bytes of the renderer's ARGB pixels and ignores the (always opaque) alpha. */
    private static final DirectColorModel RGB = new DirectColorModel(24, 0xff0000, 0x00ff00, 0x0000ff);

    private final FractalRenderer renderer;

    public FractalImageExporter(FractalRenderer renderer) {
        this.renderer = Objects.requireNonNull(renderer, "renderer");
    }

    /**
     * Renders the request and writes it to the destination, replacing any existing file.
     *
     * @return A future completed with the destination once written, or completed exceptionally with the
     * cause of the failure, such as an {@link UncheckedIOException} or an {@link OutOfMemoryError}.
     */
    public CompletableFuture<Path> export(RenderRequest request, ImageFormat format, Path destination) {
        Objects.requireNonNull(request, "request");
        Objects.requireNonNull(format, "format");
        Objects.requireNonNull(destination, "destination");
        return CompletableFuture.supplyAsync(() -> {
            int[] pixels = renderer.render(request, () -> false);
            write(toImage(pixels, request.widthPx(), request.heightPx()), format, destination);
            return destination;
        }, EXPORT_THREADS);
    }

    /** Wraps the pixels in an image without copying them, since exported images can be huge. */
    private static BufferedImage toImage(int[] pixels, int width, int height) {
        DataBufferInt buffer = new DataBufferInt(pixels, pixels.length);
        WritableRaster raster = Raster.createPackedRaster(buffer, width, height, width, RGB.getMasks(), null);
        return new BufferedImage(RGB, raster, false, null);
    }

    private static void write(BufferedImage image, ImageFormat format, Path destination) {
        // The stream is opened here rather than by ImageIO.write(image, format, File), which prints
        // the cause of failures to the standard error and replaces it with a generic message.
        // An existing file is deleted first because the stream does not truncate it.
        try {
            Files.deleteIfExists(destination);
            try (ImageOutputStream output = new FileImageOutputStream(destination.toFile())) {
                if (!ImageIO.write(image, format.extension(), output)) {
                    throw new IOException("no image writer is available for the " + format + " format");
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Could not write " + destination + ": " + e.getMessage(), e);
        }
    }
}
