package org.fraclab.ui;

import javafx.application.HostServices;
import javafx.fxml.FXML;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Window;
import org.fraclab.ui.about.AboutDialogController;
import org.fraclab.ui.export.FractalExportDialogController;
import org.fraclab.ui.navigation.GoToPositionDialogController;
import org.fraclab.ui.overlay.OverlayOptions;
import org.fraclab.ui.settings.SettingsDialogController;
import org.fraclab.ui.view.FractalViewController;

/** Handles the menus and keyboard shortcuts of the main window, and the dialogs they open. */
public final class MainWindowController implements AutoCloseable {
    private final OverlayOptions overlayOptions = new OverlayOptions();

    private final DialogWindow<SettingsDialogController> settingsDialog = new DialogWindow<>(
            SettingsDialogController.class.getResource("SettingsDialog.fxml"), "Fractal Settings");
    private final DialogWindow<GoToPositionDialogController> goToPositionDialog = new DialogWindow<>(
            GoToPositionDialogController.class.getResource("GoToPositionDialog.fxml"), "Go to position");
    private final DialogWindow<FractalExportDialogController> exportDialog = new DialogWindow<>(
            FractalExportDialogController.class.getResource("FractalExportDialog.fxml"), "Fractal Export");
    private final DialogWindow<AboutDialogController> aboutDialog = new DialogWindow<>(
            AboutDialogController.class.getResource("AboutDialog.fxml"), "About this app");

    private HostServices hostServices;

    @FXML private BorderPane root;
    @FXML private FractalViewController fractalViewController;
    @FXML private CheckMenuItem showScaleMenuItem;
    @FXML private CheckMenuItem showInfoLabelMenuItem;
    @FXML private CheckMenuItem showZoomMenuItem;
    @FXML private CheckMenuItem showCenterCoordinatesMenuItem;
    @FXML private CheckMenuItem showAxesMenuItem;

    @FXML
    private void initialize() {
        showScaleMenuItem.selectedProperty().bindBidirectional(overlayOptions.showScaleProperty());
        showInfoLabelMenuItem.selectedProperty().bindBidirectional(overlayOptions.showInfoLabelProperty());
        showZoomMenuItem.selectedProperty().bindBidirectional(overlayOptions.showZoomProperty());
        showCenterCoordinatesMenuItem.selectedProperty().bindBidirectional(overlayOptions.showCenterCoordinatesProperty());
        showAxesMenuItem.selectedProperty().bindBidirectional(overlayOptions.showAxesProperty());
        fractalViewController.bindOverlayOptions(overlayOptions);
    }

    /** Called by the application before the window is shown, to let dialogs open web pages. */
    public void setHostServices(HostServices hostServices) {
        this.hostServices = hostServices;
    }

    /** Called by the application once the window is shown and laid out. */
    public void start() {
        root.getScene().addEventFilter(KeyEvent.KEY_PRESSED, this::handleShortcut);
        fractalViewController.start();
    }

    @FXML
    private void resetView() {
        fractalViewController.resetView();
    }

    @FXML
    private void showSettings() {
        settingsDialog.show(window(), dialog -> dialog.bindTo(fractalViewController.settings()));
    }

    @FXML
    private void showGoToPosition() {
        goToPositionDialog.show(window(), dialog -> dialog.init(fractalViewController.viewport(), fractalViewController::goTo));
    }

    @FXML
    private void showExportDialog() {
        exportDialog.show(window(), dialog -> dialog.init(fractalViewController::renderRequest),
                FractalExportDialogController::onShown);
    }

    @FXML
    private void showAboutWindow() {
        aboutDialog.show(window(), dialog -> dialog.init(hostServices));
    }

    private void handleShortcut(KeyEvent event) {
        switch (event.getCode()) {
            case R -> resetView();
            case S -> showSettings();
            default -> {
                return;
            }
        }
        event.consume();
    }

    private Window window() {
        return root.getScene().getWindow();
    }

    @Override
    public void close() {
        settingsDialog.close();
        goToPositionDialog.close();
        exportDialog.close();
        aboutDialog.close();
        fractalViewController.close();
    }
}
