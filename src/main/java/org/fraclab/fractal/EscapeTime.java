package org.fraclab.fractal;

import org.fraclab.model.RenderSettings;

/**
 * Escape-time iteration shared by the Mandelbrot and Julia sets: z(n+1) = z(n)^p + c.
 * The two sets only differ in which of z(0) and c comes from the tested point (see doc/fractals.md).
 */
final class EscapeTime {
    private EscapeTime() { }

    /**
     * Iterates from z(0) until |z| exceeds the escape radius or the iteration budget is spent.
     *
     * @return The (possibly smoothed) iteration count at which the point escaped,
     * or the maximum iteration count if it never did.
     */
    static double iterationCount(double zReal, double zImaginary, double cReal, double cImaginary,
                                 RenderSettings settings) {
        ComplexNumber z = new ComplexNumber(zReal, zImaginary);
        int maxIterations = settings.maxIterations();
        double exponent = settings.exponent();
        double escapeRadius = settings.escapeRadius();

        for (int iteration = 0; iteration < maxIterations; iteration++) {
            z.powerAndAdd(exponent, cReal, cImaginary);
            double magnitude = z.magnitude();
            if (magnitude > escapeRadius) {
                return settings.smooth() ? smoothIteration(iteration, magnitude, exponent) : iteration;
            }
        }
        return maxIterations;
    }

    /**
     * Turns the integer escape iteration into a continuous value, which removes color banding.
     * See <a href="https://linas.org/art-gallery/escape/smooth.html">Renormalizing the Mandelbrot escape</a>.
     * The formula only applies when |z| grows past 1 and the exponent is above 1; otherwise the
     * integer iteration is returned unchanged.
     *
     * @param iteration Iteration count reached before escaping the fractal's radius.
     * @param magnitude Magnitude of the point when it escaped.
     * @param exponent  Exponent of the iterated polynomial.
     */
    static double smoothIteration(int iteration, double magnitude, double exponent) {
        double smoothed = iteration + 1.0 - Math.log(Math.log(magnitude)) / Math.log(exponent);
        return Double.isFinite(smoothed) ? smoothed : iteration;
    }
}
