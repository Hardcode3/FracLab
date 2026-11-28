package org.fraclab.ui;

import javafx.util.StringConverter;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Converts decimal numbers typed by users, whatever their locale habits.
 * Numbers are displayed with a period and without rounding away meaningful decimals.
 * Input accepts a comma or a period as decimal separator, ignores spaces,
 * and accepts the Unicode minus sign that some keyboards and applications produce.
 * <p>
 * As a {@link StringConverter}, invalid input converts to {@code null} instead of throwing.
 */
public final class DecimalConverter extends StringConverter<Double> {
    private final DecimalFormat format = new DecimalFormat("0.##########", DecimalFormatSymbols.getInstance(Locale.ROOT));

    @Override
    public String toString(Double value) {
        return value == null ? "" : format.format(value);
    }

    @Override
    public Double fromString(String text) {
        try {
            return text == null || text.isBlank() ? null : parse(text);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Parses a decimal number leniently.
     *
     * @throws NumberFormatException if the text is not a number.
     */
    public static double parse(String text) {
        String normalized = text.strip()
                .replace('−', '-')
                .replaceAll("\\s+", "")
                .replace(',', '.');
        return Double.parseDouble(normalized);
    }
}
