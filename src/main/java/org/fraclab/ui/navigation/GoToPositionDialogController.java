package org.fraclab.ui.navigation;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.fraclab.model.Viewport;
import org.fraclab.ui.DecimalConverter;

/** Lets the user type the center and zoom factor of the view, for instance to reach a shared position. */
public final class GoToPositionDialogController {
    /** Moves the view; rejects invalid positions with an {@link IllegalArgumentException}. */
    @FunctionalInterface
    public interface Navigator {
        void goTo(double real, double imaginary, double zoomFactor);
    }

    @FXML private TextField centerRealField;
    @FXML private TextField centerImaginaryField;
    @FXML private TextField zoomFactorField;
    @FXML private Label validationMessage;

    private Navigator navigator;

    /** Called once by the window that loads this controller, with the current view to prefill the form. */
    public void init(Viewport currentView, Navigator navigator) {
        this.navigator = navigator;
        centerRealField.setText(Double.toString(currentView.centerXCp()));
        centerImaginaryField.setText(Double.toString(currentView.centerYCp()));
        zoomFactorField.setText(Double.toString(currentView.zoomFactor()));
    }

    @FXML
    private void goToPosition() {
        double centerReal;
        double centerImaginary;
        double zoomFactor;
        try {
            centerReal = DecimalConverter.parse(centerRealField.getText());
            centerImaginary = DecimalConverter.parse(centerImaginaryField.getText());
            zoomFactor = DecimalConverter.parse(zoomFactorField.getText());
        } catch (NumberFormatException e) {
            showValidationMessage("Enter valid real and imaginary coordinates and a zoom factor.");
            return;
        }
        try {
            navigator.goTo(centerReal, centerImaginary, zoomFactor);
        } catch (IllegalArgumentException e) {
            showValidationMessage(e.getMessage());
            return;
        }
        close();
    }

    @FXML
    private void cancel() {
        close();
    }

    private void showValidationMessage(String message) {
        validationMessage.setText(message);
        validationMessage.setVisible(true);
    }

    private void close() {
        validationMessage.getScene().getWindow().hide();
    }
}
