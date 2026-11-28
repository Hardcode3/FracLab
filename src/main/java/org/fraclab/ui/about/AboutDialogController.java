package org.fraclab.ui.about;

import javafx.application.HostServices;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Properties;

/** Presents the application, its version and a link to its repository. */
public final class AboutDialogController {
    private static final String REPOSITORY_URL = "https://github.com/Hardcode3/FracLab";
    /** Generated from the build: Maven fills in the project version. */
    private static final String BUILD_INFO = "build.properties";
    private static final String VERSION = "v" + loadBuildInfo().getProperty("version", "?");

    @FXML private Label heroVersionLabel;
    @FXML private Label versionLabel;

    private HostServices hostServices;

    @FXML
    private void initialize() {
        heroVersionLabel.setText(VERSION);
        versionLabel.setText(VERSION);
    }

    /** Called once by the window that loads this controller. */
    public void init(HostServices hostServices) {
        this.hostServices = hostServices;
    }

    @FXML
    private void openGitHubRepo() {
        hostServices.showDocument(REPOSITORY_URL);
    }

    private static Properties loadBuildInfo() {
        Properties properties = new Properties();
        try (InputStream input = AboutDialogController.class.getResourceAsStream(BUILD_INFO)) {
            if (input != null) {
                properties.load(input);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Could not read " + BUILD_INFO, e);
        }
        return properties;
    }
}
