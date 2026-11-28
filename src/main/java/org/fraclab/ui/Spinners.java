package org.fraclab.ui;

import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextFormatter;
import javafx.util.StringConverter;

import java.util.function.UnaryOperator;

/**
 * Sets up editable spinners so that typed values are handled consistently.
 * <p>
 * A spinner commits its text when Enter is pressed and when it loses focus. Values outside the range are
 * clamped to it, and text that is not a number (including empty text) restores the previous value.
 * Without this, a spinner commits {@code null}, which bound numeric properties silently turn into 0.
 */
public final class Spinners {
    private static final UnaryOperator<TextFormatter.Change> DIGITS_ONLY =
            change -> change.getControlNewText().matches("\\d*") ? change : null;

    private Spinners() { }

    /** Configures a spinner of whole numbers within [min, max]; only digits can be typed. */
    public static void integer(Spinner<Integer> spinner, int min, int max) {
        var factory = new SpinnerValueFactory.IntegerSpinnerValueFactory(min, max);
        factory.setConverter(new LenientIntegerConverter());
        install(spinner, factory);
        spinner.getEditor().setTextFormatter(new TextFormatter<>(DIGITS_ONLY));
    }

    /** Configures a spinner of decimal numbers within [min, max], stepping by {@code step}. */
    public static void decimal(Spinner<Double> spinner, double min, double max, double step) {
        var factory = new SpinnerValueFactory.DoubleSpinnerValueFactory(min, max, min, step);
        factory.setConverter(new DecimalConverter());
        install(spinner, factory);
    }

    private static <T> void install(Spinner<T> spinner, SpinnerValueFactory<T> factory) {
        // Registered before any other listener, so that no one else observes the transient null.
        factory.valueProperty().addListener((_, oldValue, newValue) -> {
            if (newValue == null && oldValue != null) {
                factory.setValue(oldValue);
            }
        });
        spinner.setValueFactory(factory);
    }

    private static final class LenientIntegerConverter extends StringConverter<Integer> {
        @Override
        public String toString(Integer value) {
            return value == null ? "" : value.toString();
        }

        @Override
        public Integer fromString(String text) {
            try {
                return text == null || text.isBlank() ? null : Integer.valueOf(text.strip());
            } catch (NumberFormatException e) {
                return null;
            }
        }
    }
}
