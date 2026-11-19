package org.jfractals.ui.export;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
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

        imageExtensionCombo.getItems().setAll(FractalExportImageExtension.values());
        imageExtensionCombo.setValue(settings.getImageExtension());
        imageExtensionCombo.valueProperty().addListener((_, _, newValue) -> {
            if (newValue != null) {
                settings.setImageExtension(newValue);

                File currentExportPath = new File(exportPathTextField.getText());
                if (currentExportPath.isFile()) {
                    String fileWithNewExtension = FileUtils.replaceFileExtension(currentExportPath.toString(), newValue.toString());
                    exportPathTextField.setText(fileWithNewExtension);
                    exportPathTextField.positionCaret(fileWithNewExtension.length());
                    settings.setFpath(new File(fileWithNewExtension));
                }
            }
        });
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

    private static String getDefaultExportFileName(String extension) {
        String formattedExtension = extension;
        if (!extension.startsWith(".")) {
            formattedExtension = "." + extension;
        }
        String currentDateTime = new SimpleDateFormat("yyyy_MM_dd-HH_mm_ss-").format(new Date());
        return currentDateTime + "fractal_image" + formattedExtension;
    }

    public void setFractalViewController(FractalViewController fractalViewController) {
        this.fractalViewController = fractalViewController;
    }

    @FXML
    private void onBrowseClicked() {
        String imageExtension = FractalExportImageExtension.PNG.toString();
        if (imageExtensionCombo.getValue() != null) {
            imageExtension = imageExtensionCombo.getValue().toString();
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose a file to export the fractal");
        fileChooser.setInitialFileName(getDefaultExportFileName(imageExtension));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpeg"));
        File selectedFile = fileChooser.showSaveDialog(null);
        if (selectedFile != null) {
            String path = selectedFile.getAbsolutePath();
            exportPathTextField.setText(path);
            exportPathTextField.positionCaret(path.length());
            settings.setFpath(new File(path));
        }
    }

    @FXML void onExportClicked() {
        // TODO: Validate
        exportStatus.setText("Currently exporting...");
        FractalExportService exportService = new FractalExportService();
        exportService.export(fractalViewController.getRenderState(), settings, () -> {
            exportStatus.setText("Export completed successfully! Idle.");
        });
    }
}
