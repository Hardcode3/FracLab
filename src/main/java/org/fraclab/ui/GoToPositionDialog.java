package org.fraclab.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.fraclab.ui.view.FractalViewController;

import java.io.IOException;

public final class GoToPositionDialog implements AutoCloseable {
    private Stage stage;

    public void show(Window owner, FractalViewController fractalViewController) {
        if (stage != null && stage.isShowing()) {
            stage.toFront();
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("GoToPositionDialog.fxml"));
            Parent content = loader.load();
            loader.<GoToPositionDialogController>getController().setFractalViewController(fractalViewController);

            stage = new Stage();
            stage.setTitle("Go to position");
            stage.initOwner(owner);
            stage.setResizable(false);
            stage.setScene(new Scene(content));
            stage.setOnHidden(_ -> stage = null);
            stage.show();
        } catch (IOException e) {
            throw new IllegalStateException("Could not open the go to position dialog", e);
        }
    }

    @Override
    public void close() {
        if (stage != null) {
            stage.close();
        }
    }
}
