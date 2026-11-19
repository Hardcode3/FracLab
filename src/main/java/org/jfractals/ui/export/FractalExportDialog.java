package org.jfractals.ui.export;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import org.jfractals.ui.view.FractalViewController;

public class FractalExportDialog implements AutoCloseable {
    private static final String WIN_TITLE = "Fractal Export";

    private Stage stage;

    public void show(FractalViewController fractalViewController) {
        if (stage != null && stage.isShowing()) {
            stage.toFront();
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FractalExportDialog.fxml"));
            Parent content = loader.load();

            FractalExportDialogController controller = loader.getController();
            controller.setFractalViewController(fractalViewController);

            stage = new Stage();
            stage.setTitle(WIN_TITLE);
            stage.setResizable(false);
            stage.setScene(new Scene(content));
            stage.setOnHidden(_ -> stage = null);
            stage.setOnShown(_ -> {
                content.requestFocus();
                controller.onViewReady();
            });
            stage.show();
        } catch (IOException e) {
            throw new IllegalStateException("Could not open fractal export dialog", e);
        }
    }

    @Override
    public void close() {
        if (stage != null) stage.close();
    }
}
