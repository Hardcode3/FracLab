package org.fraclab.app;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.fraclab.ui.MainWindowController;

import java.io.IOException;
import java.net.URL;

/** The JavaFX application: creates and owns the main window. Closing it quits the application. */
public final class FracLabApp extends Application {
    private static final double INITIAL_WIN_WIDTH_PX = 720;
    private static final double INITIAL_WIN_HEIGHT_PX = 480;
    private static final String WIN_TITLE = "FracLab";

    private MainWindowController mainWindowController;

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(MainWindowController.class.getResource("MainWindow.fxml"));
        Scene scene = new Scene(loader.load(), INITIAL_WIN_WIDTH_PX, INITIAL_WIN_HEIGHT_PX);
        mainWindowController = loader.getController();
        mainWindowController.setHostServices(getHostServices());

        primaryStage.setTitle(WIN_TITLE);
        primaryStage.setScene(scene);
        URL logo = MainWindowController.class.getResource("AppLogo.png");
        if (logo != null) {
            primaryStage.getIcons().add(new Image(logo.toExternalForm()));
        }
        primaryStage.setOnShown(_ -> mainWindowController.start());
        // Dialogs must not keep the application running once the main window is gone.
        primaryStage.setOnHidden(_ -> Platform.exit());
        primaryStage.show();
    }

    @Override
    public void stop() {
        if (mainWindowController != null) {
            mainWindowController.close();
        }
    }
}
