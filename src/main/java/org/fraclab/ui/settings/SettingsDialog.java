package org.fraclab.ui.settings;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.fraclab.model.FractalSettings;

import java.io.IOException;

public final class SettingsDialog implements AutoCloseable {
    private static final String WIN_TITLE = "Fractal Settings";

    private Stage stage;

    public void show(Window owner, FractalSettings settings) {
        if (stage != null && stage.isShowing()) {
            stage.toFront();
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(SettingsDialog.class.getResource("SettingsDialog.fxml"));
            Parent content = loader.load();
            loader.<SettingsDialogController>getController().setSettings(settings);

            stage = new Stage();
            stage.setTitle(WIN_TITLE);
            stage.initOwner(owner);
            stage.setResizable(false);
            stage.setScene(new Scene(content));
            stage.setOnHidden(_ -> stage = null);
            stage.show();
        } catch (IOException e) {
            throw new IllegalStateException("Could not load the settings window", e);
        }
    }

    @Override
    public void close() {
        if (stage != null) stage.close();
    }
}
