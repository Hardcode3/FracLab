package org.fraclab.fractal;

import org.fraclab.model.RenderSettings;

/** Calculates the escape iteration count for points in a Julia set: z(0) is the point and c is a constant. */
public final class Julia {
    private Julia() { }

    public static double iterationCount(double real, double imaginary, RenderSettings settings) {
        return EscapeTime.iterationCount(real, imaginary, settings.juliaReal(), settings.juliaImag(), settings);
    }
}
