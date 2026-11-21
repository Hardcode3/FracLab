package org.fraclab.fractal;

import org.fraclab.model.RenderSettings;

/** Calculates the escape iteration count for points in a Julia set. */
public final class Julia {
    private Julia() { }

    public static double iterationCount(double real, double imaginary, RenderSettings settings) {
        double cReal = settings.juliaReal();
        double cImaginary = settings.juliaImag();
        ComplexNumber z = new ComplexNumber(real, imaginary);

        for (int iteration = 0; iteration < settings.maxIterations(); iteration++) {
            z.powerAndAdd(settings.exponent(), cReal, cImaginary);
            double magnitude = z.magnitude();
            if (magnitude > settings.escapeRadius()) {
                return FractalMaths.smoothIteration(iteration, magnitude, settings);
            }
        }
        return settings.maxIterations();
    }
}
