package org.fraclab.ui.settings;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.Property;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import org.fraclab.model.FractalSettings;
import org.fraclab.model.FractalType;
import org.fraclab.ui.Spinners;

import java.util.ArrayList;
import java.util.List;

/** Binds the settings form to the application's shared settings model. Changes apply immediately. */
public final class SettingsDialogController {
    private static final int MIN_ITERATIONS = 1;
    private static final int MAX_ITERATIONS = 10_000;
    private static final double MIN_ESCAPE_RADIUS = 0.1;
    private static final double MAX_ESCAPE_RADIUS = 1000.0;
    private static final double ESCAPE_RADIUS_STEP = 0.1;
    private static final double MIN_EXPONENT = 0.1;
    private static final double MAX_EXPONENT = 16.0;
    private static final double EXPONENT_STEP = 0.1;
    private static final double MIN_JULIA_CONSTANT = -2.0;
    private static final double MAX_JULIA_CONSTANT = 2.0;
    private static final double JULIA_CONSTANT_STEP = 0.005;

    /**
     * Object views of the numeric settings properties, bound to the spinners.
     * Bidirectional bindings only hold weak references, so without this list these views could be
     * garbage collected while the dialog is open, silently disconnecting the spinners from the settings.
     */
    private final List<Property<?>> boundProperties = new ArrayList<>();

    @FXML private ComboBox<FractalType> typeChoice;
    @FXML private Spinner<Integer> maxIterationsSpinner;
    @FXML private Spinner<Double> escapeRadiusSpinner;
    @FXML private CheckBox smoothCheckBox;
    @FXML private Spinner<Double> exponentSpinner;
    @FXML private Spinner<Double> juliaRealSpinner;
    @FXML private Spinner<Double> juliaImagSpinner;

    @FXML
    private void initialize() {
        typeChoice.getItems().setAll(FractalType.values());
        Spinners.integer(maxIterationsSpinner, MIN_ITERATIONS, MAX_ITERATIONS);
        Spinners.decimal(escapeRadiusSpinner, MIN_ESCAPE_RADIUS, MAX_ESCAPE_RADIUS, ESCAPE_RADIUS_STEP);
        Spinners.decimal(exponentSpinner, MIN_EXPONENT, MAX_EXPONENT, EXPONENT_STEP);
        Spinners.decimal(juliaRealSpinner, MIN_JULIA_CONSTANT, MAX_JULIA_CONSTANT, JULIA_CONSTANT_STEP);
        Spinners.decimal(juliaImagSpinner, MIN_JULIA_CONSTANT, MAX_JULIA_CONSTANT, JULIA_CONSTANT_STEP);

        BooleanBinding notJulia = typeChoice.valueProperty().isNotEqualTo(FractalType.JULIA);
        juliaRealSpinner.disableProperty().bind(notJulia);
        juliaImagSpinner.disableProperty().bind(notJulia);
    }

    /** Called once by the window that loads this controller. */
    public void bindTo(FractalSettings settings) {
        typeChoice.valueProperty().bindBidirectional(settings.typeProperty());
        smoothCheckBox.selectedProperty().bindBidirectional(settings.smoothProperty());
        bind(maxIterationsSpinner, settings.maxIterationsProperty().asObject());
        bind(escapeRadiusSpinner, settings.escapeRadiusProperty().asObject());
        bind(exponentSpinner, settings.exponentProperty().asObject());
        bind(juliaRealSpinner, settings.juliaRealProperty().asObject());
        bind(juliaImagSpinner, settings.juliaImagProperty().asObject());
    }

    private <T> void bind(Spinner<T> spinner, Property<T> property) {
        boundProperties.add(property);
        spinner.getValueFactory().valueProperty().bindBidirectional(property);
    }
}
