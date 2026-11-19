package org.jfractals.ui;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.CheckMenuItem;
import org.jfractals.ui.export.FractalExportDialog;
import org.jfractals.ui.overlay.OverlayOptions;
import org.jfractals.ui.view.FractalViewController;

public class MainWindowController {
    private final FractalExportDialog exportDialog = new FractalExportDialog();
    private final OverlayOptions overlayOptions = new OverlayOptions();
    @FXML private FractalViewController fractalViewController;
    @FXML private CheckMenuItem showScaleMenuItem;
    @FXML private CheckMenuItem showInfoLabel;
    @FXML private CheckMenuItem showZoomMenuItem;
    @FXML private CheckMenuItem showCenterCoordinatesMenuItem;
    @FXML private CheckMenuItem showAxesMenuItem;

    @FXML
    private void initialize() {
        showScaleMenuItem.selectedProperty().bindBidirectional(overlayOptions.getShowScaleProperty());
        showInfoLabel.selectedProperty().bindBidirectional(overlayOptions.getShowInfoLabelProperty());
        showZoomMenuItem.selectedProperty().bindBidirectional(overlayOptions.getShowZoomProperty());
        showCenterCoordinatesMenuItem.selectedProperty().bindBidirectional(overlayOptions.getShowCenterCoordinatesProperty());
        showAxesMenuItem.selectedProperty().bindBidirectional(overlayOptions.getShowAxesProperty());
        fractalViewController.setViewOptions(overlayOptions);
    }

    @FXML private void resetView() {
        fractalViewController.resetView();
    }

    @FXML private void showSettings() {
        fractalViewController.showSettings();
    }

    @FXML private void showExportDialog() {
        exportDialog.show(fractalViewController.getRenderState());
    }

    public void onViewReady(Scene scene) {
        fractalViewController.onViewReady(scene);
    }

    public void close() {
        fractalViewController.close();
    }
}
