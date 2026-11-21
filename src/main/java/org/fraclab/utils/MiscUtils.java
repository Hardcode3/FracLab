package org.fraclab.utils;

import java.awt.Desktop;
import java.net.URI;

public class MiscUtils {
    public static void openGitHubRepository() {
        final String REPO_URI = "https://github.com/Hardcode3/FracLab";
        if (!Desktop.isDesktopSupported()) {
            throw new RuntimeException("Desktop environment is not supported on this platform...");
        }
        if (!Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            throw new RuntimeException("Desktop browse operation is not supported on this platform...");
        }
        try {
            URI repoURI = new URI(REPO_URI);
            Desktop.getDesktop().browse(repoURI);
        } catch (java.net.URISyntaxException e) {
            throw new RuntimeException("Invalid repository URI syntax " + REPO_URI, e);
        } catch (java.io.IOException e) {
            throw new RuntimeException("Default browser failed to be launched or not found", e);
        }
    }
}
