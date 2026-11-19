package org.jfractals.ui.export;

import java.io.File;
import java.util.function.IntConsumer;
import java.util.function.UnaryOperator;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import org.jfractals.ui.view.FractalViewController;
import org.jfractals.utils.FileUtils;

public class FractalExportDialogController {
    private final FractalExportSettings settings = new FractalExportSettings();
    private FractalViewController fractalViewController;
    private boolean syncingControls;
    private static final UnaryOperator<TextFormatter.Change> SPINNER_INTEGER_FILTER = change -> change.getControlNewText().matches("\\d{0,6}") ? change : null;

    @FXML private TextField exportPathTextField;
    @FXML private Spinner<Integer> widthSpinner;
    @FXML private Spinner<Integer> heightSpinner;
    @FXML private Spinner<Integer> aspectRatioWidthSpinner;
    @FXML private Spinner<Integer> aspectRatioHeightSpinner;
    @FXML private ComboBox<FractalExportImageExtension> imageExtensionCombo;
    @FXML private Label exportStatus;

    @FXML
    private void initialize() {
        widthSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(100, Integer.MAX_VALUE));
        widthSpinner.getEditor().setTextFormatter(new TextFormatter<String>(SPINNER_INTEGER_FILTER));
        heightSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(100, Integer.MAX_VALUE));
        heightSpinner.getEditor().setTextFormatter(new TextFormatter<String>(SPINNER_INTEGER_FILTER));
        aspectRatioWidthSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Integer.MAX_VALUE));
        aspectRatioWidthSpinner.getEditor().setTextFormatter(new TextFormatter<String>(SPINNER_INTEGER_FILTER));
        aspectRatioHeightSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Integer.MAX_VALUE));
        aspectRatioHeightSpinner.getEditor().setTextFormatter(new TextFormatter<String>(SPINNER_INTEGER_FILTER));

        connect(widthSpinner, settings::setWidthPx);
        connect(heightSpinner, settings::setHeightPx);
        connect(aspectRatioWidthSpinner, settings::setAspectRatioWidth);
        connect(aspectRatioHeightSpinner, settings::setAspectRatioHeight);

        imageExtensionCombo.getItems().setAll(FractalExportImageExtension.values());
        imageExtensionCombo.setValue(settings.getImageExtension());
        imageExtensionCombo.valueProperty().addListener((_, _, newValue) -> updateExportPathExtension(newValue));

        exportPathTextField.textProperty().addListener((_, _, newValue) -> settings.setFPath(newValue));
    }

    public void onViewReady() {
        refreshControlsFromSettings();
        updateExportPathTextField(settings.getFpathString());
    }

    private void connect(Spinner<Integer> spinner, IntConsumer setter) {
        spinner.getValueFactory().valueProperty().addListener((_, _, newValue) -> {
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

    public void setFractalViewController(FractalViewController fractalViewController) {
        this.fractalViewController = fractalViewController;
    }

    private void updateExportPathTextField(String newPath) {
        exportPathTextField.setText(newPath);
        exportPathTextField.positionCaret(newPath.length());
    }

    private void updateExportPathExtension(FractalExportImageExtension newExtension) {
        if (newExtension == null) {
            return;
        }
        settings.setImageExtension(newExtension);
        String fileWithNewExtension = FileUtils.replaceFileExtension(settings.getFpathString(), newExtension.toString());
        updateExportPathTextField(fileWithNewExtension);
    }

    @FXML
    private void onBrowseClicked() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose a file to export the fractal");
        fileChooser.setInitialDirectory(settings.getFpath().getParentFile());
        fileChooser.setInitialFileName(settings.getFpath().getName());
        var filter = new FileChooser.ExtensionFilter("Images", FractalExportImageExtension.getAllPatterns());
        fileChooser.getExtensionFilters().add(filter);
        File selectedFile = fileChooser.showSaveDialog(null);
        if (selectedFile == null) {
            return;
        }
        String path = selectedFile.getAbsolutePath();
        updateExportPathTextField(path);
    }

    @FXML void onExportClicked() {
        if (!settings.isFPathValid()) {
            return;
        }
        exportStatus.setText("Currently exporting...");
        var exportService = new FractalExportService();
        Runnable onRendered = () -> exportStatus.setText("Export completed successfully! Idle.");
        exportService.export(fractalViewController.getRenderState(), settings, onRendered);
    }
}
