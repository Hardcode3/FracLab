package org.fraclab.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AboutDialog implements AutoCloseable {
    private static final String WIN_TITLE = "About this app";
    private static final String FXML_FNAME = "AboutDialog.fxml";

    private Stage stage;

    public void show() {
        if (stage != null && stage.isShowing()) {
            return;
        }
        try {
            var loader = new FXMLLoader(getClass().getResource(FXML_FNAME));
            Parent content = loader.load();

            stage = new Stage();
            stage.setTitle(WIN_TITLE);
            stage.setResizable(false);
            stage.setScene(new Scene(content));
            stage.setOnHidden(_ -> stage = null);
            stage.show();
        } catch (java.io.IOException e) {
            throw new IllegalStateException("Could not open about this app dialog", e);
        }
    }

    @Override
    public void close() {
        if (stage != null) {
            stage.close();
        }
    }
}
