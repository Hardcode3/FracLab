package org.fraclab.ui.export;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import org.fraclab.export.FractalImageExporter;
import org.fraclab.export.ImageFormat;
import org.fraclab.render.FractalRenderer;
import org.fraclab.render.RenderRequest;
import org.fraclab.ui.Spinners;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.concurrent.CompletionException;
import java.util.function.IntConsumer;
import java.util.function.Supplier;

/**
 * Exports the current view of the fractal to an image file, at a size and in a format chosen by the user.
 * The view is captured when the export starts, and the export runs in the background.
 */
public final class FractalExportDialogController {
    private static final System.Logger LOGGER = System.getLogger(FractalExportDialogController.class.getName());

    private final FractalImageExporter exporter = new FractalImageExporter(new FractalRenderer());

    private FractalExportSettings settings;
    private Supplier<RenderRequest> currentView;
    private boolean syncingControls;

    @FXML private TextField exportPathTextField;
    @FXML private Spinner<Integer> widthSpinner;
    @FXML private Spinner<Integer> heightSpinner;
    @FXML private Spinner<Integer> aspectRatioWidthSpinner;
    @FXML private Spinner<Integer> aspectRatioHeightSpinner;
    @FXML private ComboBox<ImageFormat> imageFormatCombo;
    @FXML private Label exportStatus;
    @FXML private Button exportButton;

    @FXML
    private void initialize() {
        // Defaults to an image of the size of the screen.
        Rectangle2D screen = Screen.getPrimary().getBounds();
        settings = new FractalExportSettings((int) Math.round(screen.getWidth()), (int) Math.round(screen.getHeight()),
                FractalExportSettings.defaultOutputFile());

        Spinners.integer(widthSpinner, FractalExportSettings.MIN_SIZE_PX, FractalExportSettings.MAX_SIZE_PX);
        Spinners.integer(heightSpinner, FractalExportSettings.MIN_SIZE_PX, FractalExportSettings.MAX_SIZE_PX);
        Spinners.integer(aspectRatioWidthSpinner, FractalExportSettings.MIN_RATIO_TERM, FractalExportSettings.MAX_RATIO_TERM);
        Spinners.integer(aspectRatioHeightSpinner, FractalExportSettings.MIN_RATIO_TERM, FractalExportSettings.MAX_RATIO_TERM);
        refreshSizeControls();
        connect(widthSpinner, settings::setWidthPx);
        connect(heightSpinner, settings::setHeightPx);
        connect(aspectRatioWidthSpinner, settings::setAspectRatioWidth);
        connect(aspectRatioHeightSpinner, settings::setAspectRatioHeight);

        imageFormatCombo.getItems().setAll(ImageFormat.values());
        imageFormatCombo.setValue(settings.getFormat());
        imageFormatCombo.valueProperty().addListener((_, _, newFormat) -> changeFormat(newFormat));

        exportPathTextField.setText(settings.getOutputFile().toString());
        exportPathTextField.textProperty().addListener((_, _, newText) -> settings.setOutputFile(toPath(newText)));
    }

    /** Called once by the window that loads this controller, with what to export. */
    public void init(Supplier<RenderRequest> currentView) {
        this.currentView = currentView;
    }

    /** Called once the dialog is shown, so that the end of the file name is visible and no field has focus. */
    public void onShown() {
        exportPathTextField.getScene().getRoot().requestFocus();
        exportPathTextField.end();
    }

    private void connect(Spinner<Integer> spinner, IntConsumer setter) {
        spinner.getValueFactory().valueProperty().addListener((_, _, newValue) -> {
            if (syncingControls || newValue == null) {
                return;
            }
            setter.accept(newValue);
            refreshSizeControls();
        });
    }

    /** Shows the size and ratio, which may have been adjusted by the settings after an edit. */
    private void refreshSizeControls() {
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

    private void changeFormat(ImageFormat newFormat) {
        if (newFormat == null) {
            return;
        }
        settings.setFormat(newFormat);
        Path outputFile = settings.getOutputFile();
        if (outputFile != null) {
            showOutputFile(newFormat.applyTo(outputFile));
        }
    }

    private void showOutputFile(Path file) {
        exportPathTextField.setText(file.toString());
        exportPathTextField.end();
    }

    @FXML
    private void onBrowseClicked() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose a file to export the fractal");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", ImageFormat.allFilePatterns()));
        Path outputFile = settings.getOutputFile();
        if (outputFile != null) {
            Path folder = outputFile.toAbsolutePath().getParent();
            if (folder != null && Files.isDirectory(folder)) {
                fileChooser.setInitialDirectory(folder.toFile());
            }
            fileChooser.setInitialFileName(String.valueOf(outputFile.getFileName()));
        }
        File selectedFile = fileChooser.showSaveDialog(exportButton.getScene().getWindow());
        if (selectedFile == null) {
            return;
        }
        // The file content must match its extension: follow a known extension, otherwise add the current one.
        Path selected = selectedFile.toPath();
        ImageFormat.fromFileName(selectedFile.getName()).ifPresentOrElse(
                format -> {
                    showOutputFile(selected);
                    imageFormatCombo.setValue(format);
                },
                () -> showOutputFile(settings.getFormat().applyTo(selected)));
    }

    @FXML
    private void onExportClicked() {
        if (!settings.isOutputFileValid()) {
            exportStatus.setText("Choose a file name in an existing folder you can write to.");
            return;
        }
        RenderRequest request = currentView.get().withSize(settings.getWidthPx(), settings.getHeightPx());
        exportButton.setDisable(true);
        exportStatus.setText("Currently exporting...");
        exporter.export(request, settings.getFormat(), settings.getOutputFile())
                .whenCompleteAsync((_, error) -> onExportFinished(request, error), Platform::runLater);
    }

    private void onExportFinished(RenderRequest request, Throwable error) {
        exportButton.setDisable(false);
        if (error == null) {
            exportStatus.setText("Export completed successfully! Idle.");
            return;
        }
        Throwable cause = error instanceof CompletionException && error.getCause() != null ? error.getCause() : error;
        LOGGER.log(System.Logger.Level.ERROR, "Fractal export failed", cause);
        String reason = cause instanceof OutOfMemoryError
                ? "not enough memory for a " + request.widthPx() + " × " + request.heightPx() + " image."
                : String.valueOf(cause.getMessage());
        exportStatus.setText("Export failed: " + reason);
    }

    private static Path toPath(String text) {
        try {
            return text == null || text.isBlank() ? null : Path.of(text.strip());
        } catch (InvalidPathException e) {
            return null;
        }
    }
}
