package org.fraclab.render;

import javafx.application.Platform;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;

import java.util.Objects;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

/**
 * Renders fractal images off the JavaFX Application Thread for an interactive view.
 * Only the most recent request matters: submitting a new one cancels the one in progress,
 * and results of superseded requests are never published.
 * <p>
 * All public methods must be called on the JavaFX Application Thread.
 */
public final class FractalRenderService implements AutoCloseable {
    private static final System.Logger LOGGER = System.getLogger(FractalRenderService.class.getName());

    private final FractalRenderer renderer;

    /** Dedicated single-thread background executor running a daemon thread. */
    private final ExecutorService executor = Executors.newSingleThreadExecutor(task -> {
        Thread thread = new Thread(task, "fractal-renderer");
        thread.setDaemon(true);
        return thread;
    });

    /** Monotonically increasing counter identifying the latest request; older ones are stale. */
    private final AtomicLong generation = new AtomicLong();

    private Future<?> currentRender;
    private boolean closed;

    public FractalRenderService(FractalRenderer renderer) {
        this.renderer = Objects.requireNonNull(renderer, "renderer");
    }

    /**
     * Starts rendering in the background, superseding any render in progress.
     *
     * @param request    What to render.
     * @param onRendered Called on the JavaFX Application Thread with the image, unless a newer request
     *                   was submitted or the service was closed in the meantime.
     */
    public void render(RenderRequest request, Consumer<WritableImage> onRendered) {
        Objects.requireNonNull(request, "request");
        Objects.requireNonNull(onRendered, "onRendered");
        if (closed) {
            return;
        }

        long requestGeneration = generation.incrementAndGet();
        if (currentRender != null) {
            currentRender.cancel(true);
        }

        // Evaluated from the renderer's worker threads too: interruption only reaches this executor's
        // thread, while the generation check reaches every thread.
        BooleanSupplier isCancelled = () -> generation.get() != requestGeneration || Thread.currentThread().isInterrupted();
        currentRender = executor.submit(() -> {
            int[] pixels;
            try {
                pixels = renderer.render(request, isCancelled);
            } catch (CancellationException ignored) {
                return;
            } catch (RuntimeException exception) {
                LOGGER.log(System.Logger.Level.ERROR, "Fractal render failed", exception);
                return;
            }
            Platform.runLater(() -> {
                if (closed || generation.get() != requestGeneration) {
                    return;
                }
                onRendered.accept(toImage(pixels, request.widthPx(), request.heightPx()));
            });
        });
    }

    private static WritableImage toImage(int[] pixels, int width, int height) {
        WritableImage image = new WritableImage(width, height);
        image.getPixelWriter().setPixels(0, 0, width, height, PixelFormat.getIntArgbInstance(), pixels, 0, width);
        return image;
    }

    /** Cancels any render in progress and stops the background thread. Later requests are ignored. */
    @Override
    public void close() {
        closed = true;
        generation.incrementAndGet();
        if (currentRender != null) {
            currentRender.cancel(true);
        }
        executor.shutdownNow();
    }
}
