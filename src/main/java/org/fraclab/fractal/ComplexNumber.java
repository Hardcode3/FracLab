package org.fraclab.fractal;

/** Mutable complex value used by the escape-time iteration loops. */
final class ComplexNumber {
    private double real;
    private double imaginary;

    ComplexNumber() {
        this(0.0, 0.0);
    }

    ComplexNumber(double real, double imaginary) {
        this.real = real;
        this.imaginary = imaginary;
    }

    double magnitude() {
        // Math.hypot guards against overflow, which cannot happen here: points escape long before.
        // It is also several times slower, and this method runs in the innermost rendering loop.
        return Math.sqrt(real * real + imaginary * imaginary);
    }

    double real() {
        return real;
    }

    double imaginary() {
        return imaginary;
    }

    /** Replaces this value with z^exponent + c, using the principal complex power. */
    void powerAndAdd(double exponent, double cReal, double cImaginary) {
        if (real == 0.0 && imaginary == 0.0) {
            real = cReal;
            imaginary = cImaginary;
            return;
        }

        if (exponent == 2.0) {
            double oldReal = real;
            double oldImaginary = imaginary;
            real = oldReal * oldReal - oldImaginary * oldImaginary + cReal;
            imaginary = 2.0 * oldReal * oldImaginary + cImaginary;
            return;
        }

        double magnitude = magnitude();
        double poweredMagnitude = Math.pow(magnitude, exponent);
        double poweredAngle = Math.atan2(imaginary, real) * exponent;
        real = poweredMagnitude * Math.cos(poweredAngle) + cReal;
        imaginary = poweredMagnitude * Math.sin(poweredAngle) + cImaginary;
    }
}
