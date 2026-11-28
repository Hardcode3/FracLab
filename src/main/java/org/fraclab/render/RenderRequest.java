package org.fraclab.render;

import org.fraclab.model.RenderSettings;
import org.fraclab.model.Viewport;

import java.util.Objects;

/**
 * Everything needed to render one image: which region of the complex plane, at which pixel size,
 * and with which fractal settings. Immutable, so it can be handed to background threads.
 */
public record RenderRequest(Viewport viewport, RenderSettings settings) {
    public RenderRequest {
        Objects.requireNonNull(viewport, "viewport");
        Objects.requireNonNull(settings, "settings");
    }

    /** Width of the rendered image, in whole pixels. */
    public int widthPx() {
        return (int) viewport.widthPx();
    }

    /** Height of the rendered image, in whole pixels. */
    public int heightPx() {
        return (int) viewport.heightPx();
    }

    /** The same region and settings, rendered at another pixel size (the view height is kept). */
    public RenderRequest withSize(int widthPx, int heightPx) {
        return new RenderRequest(viewport.withSize(widthPx, heightPx), settings);
    }
}
