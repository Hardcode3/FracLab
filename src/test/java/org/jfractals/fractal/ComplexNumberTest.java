package org.jfractals.fractal;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ComplexNumberTest {
    private static final double EPSILON = 1e-12;

    @Test
    void magnitudeUsesBothComponents() {
        assertEquals(5.0, new ComplexNumber(3.0, 4.0).magnitude(), EPSILON);
    }

    @Test
    void zeroIsReplacedByTheAddendBeforePowering() {
        ComplexNumber value = new ComplexNumber();

        value.powerAndAdd(0.5, 3.0, -4.0);

        assertEquals(3.0, value.real(), EPSILON);
        assertEquals(-4.0, value.imaginary(), EPSILON);
    }

    @Test
    void exponentTwoSquaresAndAddsTheConstant() {
        ComplexNumber value = new ComplexNumber(1.0, 2.0);

        value.powerAndAdd(2.0, -0.5, 0.25);

        assertEquals(-3.5, value.real(), EPSILON);
        assertEquals(4.25, value.imaginary(), EPSILON);
    }

    @Test
    void fractionalExponentUsesThePrincipalComplexPower() {
        ComplexNumber value = new ComplexNumber(1.0, 1.0);
        double rootMagnitude = Math.pow(Math.sqrt(2.0), 0.5);
        double rootAngle = Math.PI / 8.0;

        value.powerAndAdd(0.5, 0.25, -0.5);

        assertEquals(rootMagnitude * Math.cos(rootAngle) + 0.25, value.real(), EPSILON);
        assertEquals(rootMagnitude * Math.sin(rootAngle) - 0.5, value.imaginary(), EPSILON);
    }

    @Test
    void repeatedIterationsUpdateTheSameValue() {
        ComplexNumber value = new ComplexNumber();

        value.powerAndAdd(2.0, 1.0, 0.0);
        value.powerAndAdd(2.0, 1.0, 0.0);
        value.powerAndAdd(2.0, 1.0, 0.0);

        // Mandelbrot recurrence from c = 1: 0 -> 1 -> 2 -> 5.
        assertEquals(5.0, value.real(), EPSILON);
        assertEquals(0.0, value.imaginary(), EPSILON);
    }
}
