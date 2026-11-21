package org.fraclab.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.Image;

import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import org.fraclab.utils.MiscUtils;

import java.net.URL;

public class AboutDialogController {
    @FXML private ImageView appLogoImageView;
    @FXML private GridPane headerGridPane;

    @FXML private void initialize() {
        appLogoImageView.fitHeightProperty().bind(headerGridPane.heightProperty());
        loadAppLogoImage();
    }

    private void loadAppLogoImage() {
        URL logoURL = getClass().getResource("AppLogo.png");
        if (logoURL == null) {
            throw new RuntimeException("Could not load app logo in about dialog, skipping...");
        }
        var appLogoImage = new Image(logoURL.toString());
        appLogoImageView.setImage(appLogoImage);
    }

    public void openGitHubRepo(ActionEvent actionEvent) {
        MiscUtils.openGitHubRepository();
    }
}
