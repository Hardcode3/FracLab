package org.fraclab.ui.overlay;

import java.util.Locale;

/**
 * Number formatting shared by the overlays.
 * Always uses a period as decimal separator: with a comma, as in some locales,
 * a complex number such as "0,5,0,25i" would be ambiguous.
 */
final class OverlayFormat {
    private OverlayFormat() { }

    static String complex(double real, double imaginary) {
        return String.format(Locale.ROOT, "%.6f %+.6fi", real, imaginary);
    }

    static String format(String pattern, Object... arguments) {
        return String.format(Locale.ROOT, pattern, arguments);
    }
}
