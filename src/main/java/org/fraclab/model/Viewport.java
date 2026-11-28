package org.fraclab.model;

/**
 * The region of the complex plane shown in a view of a given pixel size.
 * <p>
 * Screen coordinates have their origin at the top-left corner and grow rightwards and downwards.
 * Complex coordinates follow the mathematical convention: the real axis grows rightwards and the
 * imaginary axis grows upwards. Pixels are square, so the visible width in the complex plane
 * follows from the height and the pixel aspect ratio.
 * <p>
 * Invalid sizes (non-finite, zero or negative) fall back to the defaults, so a viewport is always usable.
 *
 * @param centerXCp Real coordinate of the view center.
 * @param centerYCp Imaginary coordinate of the view center.
 * @param widthPx   Width of the view in pixels.
 * @param heightPx  Height of the view in pixels.
 * @param heightCp  Height of the view in complex plane units.
 */
public record Viewport(
        double centerXCp, double centerYCp,
        double widthPx, double heightPx,
        double heightCp
) {
    public static final double DEFAULT_WIDTH_PX = 800;
    public static final double DEFAULT_HEIGHT_PX = 600;
    public static final double DEFAULT_HEIGHT_CP = 3.5;
    public static final double DEFAULT_CENTER_X_CP = 0.0;
    public static final double DEFAULT_CENTER_Y_CP = 0.0;

    public Viewport {
        centerXCp = Double.isFinite(centerXCp) ? centerXCp : DEFAULT_CENTER_X_CP;
        centerYCp = Double.isFinite(centerYCp) ? centerYCp : DEFAULT_CENTER_Y_CP;
        widthPx = isPositiveFinite(widthPx) ? widthPx : DEFAULT_WIDTH_PX;
        heightPx = isPositiveFinite(heightPx) ? heightPx : DEFAULT_HEIGHT_PX;
        heightCp = isPositiveFinite(heightCp) ? heightCp : DEFAULT_HEIGHT_CP;
    }

    /** The default view, before the actual size of the screen area is known. */
    public static Viewport initial() {
        return new Viewport(DEFAULT_CENTER_X_CP, DEFAULT_CENTER_Y_CP, DEFAULT_WIDTH_PX, DEFAULT_HEIGHT_PX, DEFAULT_HEIGHT_CP);
    }

    /** The default center and zoom, keeping the current pixel size. */
    public Viewport reset() {
        return new Viewport(DEFAULT_CENTER_X_CP, DEFAULT_CENTER_Y_CP, widthPx, heightPx, DEFAULT_HEIGHT_CP);
    }

    /** The same region center and height, shown on an area of another pixel size. */
    public Viewport withSize(double newWidthPx, double newHeightPx) {
        return new Viewport(centerXCp, centerYCp, newWidthPx, newHeightPx, heightCp);
    }

    /**
     * A view of the same pixel size centered on the given point, at the given zoom factor
     * (1.0 being the default view).
     *
     * @throws IllegalArgumentException if a coordinate is not finite, or the zoom factor is not
     *                                  strictly positive or leads to an unsupported view height.
     */
    public Viewport centeredAt(double centerXCp, double centerYCp, double zoomFactor) {
        if (!Double.isFinite(centerXCp) || !Double.isFinite(centerYCp)) {
            throw new IllegalArgumentException("Coordinates must be finite numbers.");
        }
        if (!isPositiveFinite(zoomFactor)) {
            throw new IllegalArgumentException("Zoom factor must be greater than zero.");
        }
        double newHeightCp = DEFAULT_HEIGHT_CP / zoomFactor;
        if (!isPositiveFinite(newHeightCp)) {
            throw new IllegalArgumentException("Zoom factor is outside the supported range.");
        }
        return new Viewport(centerXCp, centerYCp, widthPx, heightPx, newHeightCp);
    }

    public double widthCp() {
        return widthPx * unitsPerPixel();
    }

    public double unitsPerPixel() {
        return heightCp / heightPx;
    }

    /** Magnification relative to the default view. */
    public double zoomFactor() {
        return DEFAULT_HEIGHT_CP / heightCp;
    }

    public double toComplexX(double screenXPx) {
        return centerXCp + (screenXPx - widthPx / 2.0) * unitsPerPixel();
    }

    public double toComplexY(double screenYPx) {
        return centerYCp - (screenYPx - heightPx / 2.0) * unitsPerPixel();
    }

    public double toScreenX(double complexXCp) {
        return widthPx / 2.0 + (complexXCp - centerXCp) / unitsPerPixel();
    }

    public double toScreenY(double complexYCp) {
        return heightPx / 2.0 - (complexYCp - centerYCp) / unitsPerPixel();
    }

    /** The view after the content has been dragged by the given screen offset. */
    public Viewport panned(double dxPx, double dyPx) {
        double upp = unitsPerPixel();
        return new Viewport(
                centerXCp - dxPx * upp,
                centerYCp + dyPx * upp,
                widthPx, heightPx, heightCp
        );
    }

    /**
     * The view magnified by {@code zoom} around a screen point, which keeps pointing at the same
     * complex value. A zoom below 1 zooms out. Invalid zooms leave the view unchanged.
     */
    public Viewport zoomedAt(double xPx, double yPx, double zoom) {
        if (!isPositiveFinite(zoom)) {
            return this;
        }
        double real = toComplexX(xPx);
        double imaginary = toComplexY(yPx);

        double newHeightCp = heightCp / zoom;
        double newUnitsPerPixel = newHeightCp / heightPx;

        double offsetToScreenCenterXPx = xPx - widthPx / 2.0;
        double offsetToScreenCenterYPx = yPx - heightPx / 2.0;

        double newCenterXCp = real - offsetToScreenCenterXPx * newUnitsPerPixel;
        double newCenterYCp = imaginary + offsetToScreenCenterYPx * newUnitsPerPixel;

        return new Viewport(newCenterXCp, newCenterYCp, widthPx, heightPx, newHeightCp);
    }

    private static boolean isPositiveFinite(double value) {
        return Double.isFinite(value) && value > 0.0;
    }
}
