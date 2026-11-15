package org.jfractals.ui;

import java.io.File;
import java.util.function.IntConsumer;
import java.util.function.UnaryOperator;

import javafx.fxml.FXML;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.FileChooser;

public class FractalExportDialogController {
    private final FractalExportSettings settings = new FractalExportSettings();
    private boolean syncingControls;
    private static final UnaryOperator<TextFormatter.Change> SPINNER_INTEGER_FILTER = change -> change.getControlNewText().matches("\\d{0,6}") ? change : null;

    @FXML private TextField exportPathTextField;
    @FXML private Spinner<Integer> widthSpinner;
    @FXML private Spinner<Integer> heightSpinner;
    @FXML private Spinner<Integer> aspectRatioWidthSpinner;
    @FXML private Spinner<Integer> aspectRatioHeightSpinner;

    @FXML
    private void initialize() {
        widthSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(100, Integer.MAX_VALUE, 1920));
        widthSpinner.getEditor().setTextFormatter(new TextFormatter<String>(SPINNER_INTEGER_FILTER));
        heightSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(100, Integer.MAX_VALUE, 1080));
        heightSpinner.getEditor().setTextFormatter(new TextFormatter<String>(SPINNER_INTEGER_FILTER));
        aspectRatioWidthSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Integer.MAX_VALUE, 16));
        aspectRatioWidthSpinner.getEditor().setTextFormatter(new TextFormatter<String>(SPINNER_INTEGER_FILTER));
        aspectRatioHeightSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Integer.MAX_VALUE, 9));
        aspectRatioHeightSpinner.getEditor().setTextFormatter(new TextFormatter<String>(SPINNER_INTEGER_FILTER));

        connect(widthSpinner, settings::setWidthPx);
        connect(heightSpinner, settings::setHeightPx);
        connect(aspectRatioWidthSpinner, settings::setAspectRatioWidth);
        connect(aspectRatioHeightSpinner, settings::setAspectRatioHeight);
    }

    private void connect(Spinner<Integer> spinner, IntConsumer setter) {
        spinner.getValueFactory().valueProperty().addListener((obs, oldValue, newValue) -> {
            if (syncingControls || newValue == null) {
                return;
            }
            setter.accept(newValue);
            refreshControlsFromSettings();
        });
    }

    private void refreshControlsFromSettings() {
        syncingControls = true;
        try {
            widthSpinner.getValueFactory().setValue(settings.getWidthPx());
            heightSpinner.getValueFactory().setValue(settings.getHeightPx());
            aspectRatioWidthSpinner.getValueFactory().setValue(settings.getAspectRatioWidth());
            aspectRatioHeightSpinner.getValueFactory().setValue(settings.getAspectRatioHeight());
        } finally {
            syncingControls = false;
        }
    }

    @FXML
    private void onBrowseClicked() {
        FileChooser fchooser = new FileChooser();
        fchooser.setTitle("Choose a file to export the fractal");
        fchooser.setInitialFileName("fractal_image.png");
        fchooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png"));
        File selectedFile = fchooser.showSaveDialog(null);
        if (selectedFile != null) {
            String path = selectedFile.getAbsolutePath();
            exportPathTextField.setText(path);
            exportPathTextField.positionCaret(path.length());
        }
    }

    @FXML void onExportClicked() {
        String exportPath = exportPathTextField.getText();
        if (exportPath != null && !exportPath.isEmpty()) {
            // TODO: Implement the actual export logic here
            System.out.println("Exporting fractal to: " + exportPath);
        } else {
            System.out.println("Export path is empty.");
        }
    }
}
