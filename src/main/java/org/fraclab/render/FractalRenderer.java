package org.fraclab.render;

import org.fraclab.fractal.Julia;
import org.fraclab.fractal.Mandelbrot;
import org.fraclab.model.RenderSettings;
import org.fraclab.model.Viewport;

import java.util.concurrent.CancellationException;
import java.util.function.BooleanSupplier;
import java.util.stream.IntStream;

/**
 * Computes an image's pixels for various fractals without depending on JavaFX or UI state.
 * Rows are computed in parallel and the computation can be cancelled cooperatively.
 */
public final class FractalRenderer {
    /** How often, in pixels, a row checks whether the render was cancelled. */
    private static final int CANCELLATION_CHECK_INTERVAL_PX = 128;

    /**
     * Computes an opaque grayscale ARGB pixel array in row-major order.
     *
     * @param request   The region, pixel size and settings to render.
     * @param cancelled Queried periodically, from several threads, to abort the computation.
     * @return The pixels, {@code request.widthPx() * request.heightPx()} of them.
     * @throws IllegalArgumentException if the image is empty or too large to fit in an array.
     * @throws CancellationException    if {@code cancelled} reported a cancellation.
     */
    public int[] render(RenderRequest request, BooleanSupplier cancelled) {
        int width = request.widthPx();
        int height = request.heightPx();
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Image dimensions must be positive");
        }
        long pixelCount = (long) width * height;
        if (pixelCount > Integer.MAX_VALUE - 8) {
            throw new IllegalArgumentException("Image of " + width + " × " + height + " pixels is too large");
        }

        Viewport viewport = request.viewport();
        RenderSettings settings = request.settings();
        int[] pixels = new int[(int) pixelCount];

        IntStream.range(0, height).parallel().forEach(y -> {
            if (cancelled.getAsBoolean()) {
                return;
            }
            double imaginary = viewport.toComplexY(y);
            for (int x = 0; x < width; x++) {
                if (x % CANCELLATION_CHECK_INTERVAL_PX == 0 && cancelled.getAsBoolean()) {
                    return;
                }
                double real = viewport.toComplexX(x);
                double iterations = switch (settings.type()) {
                    case MANDELBROT -> Mandelbrot.iterationCount(real, imaginary, settings);
                    case JULIA -> Julia.iterationCount(real, imaginary, settings);
                };
                pixels[y * width + x] = grayscale(iterations, settings.maxIterations());
            }
        });
        if (cancelled.getAsBoolean()) {
            throw new CancellationException("Render was superseded");
        }
        return pixels;
    }

    /**
     * Converts a raw iteration count into an opaque grayscale ARGB integer pixel value.
     *
     * @param iterations    The escape iteration count computed for the pixel.
     * @param maxIterations The maximum allowed iterations used to normalize the value.
     * @return An ARGB integer representing a fully opaque shade of gray.
     */
    private static int grayscale(double iterations, int maxIterations) {
        double normalized = iterations / maxIterations;
        if (!Double.isFinite(normalized) || normalized < 0.0) {
            normalized = 0.0;
        } else if (normalized > 1.0) {
            normalized = 1.0;
        }
        int gray = (int) Math.round(normalized * 255.0);
        return 0xff000000 | gray << 16 | gray << 8 | gray;
    }
}
