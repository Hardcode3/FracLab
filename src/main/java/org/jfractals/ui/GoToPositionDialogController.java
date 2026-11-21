package org.jfractals.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Window;
import org.jfractals.ui.view.FractalViewController;
import org.jfractals.ui.view.Viewport;

public final class GoToPositionDialogController {
    @FXML private TextField centerRealField;
    @FXML private TextField centerImaginaryField;
    @FXML private TextField zoomFactorField;
    @FXML private Label validationMessage;

    private FractalViewController fractalViewController;

    public void setFractalViewController(FractalViewController fractalViewController) {
        this.fractalViewController = fractalViewController;
        Viewport viewport = fractalViewController.getViewport();
        centerRealField.setText(Double.toString(viewport.centerXCp()));
        centerImaginaryField.setText(Double.toString(viewport.centerYCp()));
        zoomFactorField.setText(Double.toString(viewport.zoomFactor()));
    }

    @FXML
    private void goToPosition() {
        try {
            double centerReal = parseNumber(centerRealField.getText());
            double centerImaginary = parseNumber(centerImaginaryField.getText());
            double zoomFactor = parseNumber(zoomFactorField.getText());
            fractalViewController.goToPosition(centerReal, centerImaginary, zoomFactor);
            close();
        } catch (IllegalArgumentException e) {
            validationMessage.setText(e instanceof NumberFormatException
                    ? "Enter valid real and imaginary coordinates and a zoom factor."
                    : e.getMessage());
            validationMessage.setManaged(true);
            validationMessage.setVisible(true);
        }
    }

    @FXML
    private void cancel() {
        close();
    }

    private void close() {
        Window window = validationMessage.getScene().getWindow();
        window.hide();
    }

    private static double parseNumber(String input) {
        String normalized = input.trim()
                .replace('\u2212', '-')
                .replaceAll("\\s+", "")
                .replace(',', '.');
        return Double.parseDouble(normalized);
    }
}
