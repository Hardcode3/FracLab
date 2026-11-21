package org.fraclab.fractal;

import org.fraclab.model.RenderSettings;

/** Calculates the escape iteration count for points in the Mandelbrot set. */
public final class Mandelbrot {
    private Mandelbrot() { }

    public static double iterationCount(double real, double imaginary, RenderSettings settings) {
        ComplexNumber z = new ComplexNumber();

        for (int iteration = 0; iteration < settings.maxIterations(); iteration++) {
            z.powerAndAdd(settings.exponent(), real, imaginary);
            double magnitude = z.magnitude();
            if (magnitude > settings.escapeRadius()) {
                return FractalMaths.smoothIteration(iteration, magnitude, settings);
            }
        }
        return settings.maxIterations();
    }
}
