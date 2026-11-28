package org.fraclab.fractal;

import org.fraclab.model.RenderSettings;

/** Calculates the escape iteration count for points in the Mandelbrot set: z(0) = 0 and c is the point. */
public final class Mandelbrot {
    private Mandelbrot() { }

    public static double iterationCount(double real, double imaginary, RenderSettings settings) {
        return EscapeTime.iterationCount(0.0, 0.0, real, imaginary, settings);
    }
}
