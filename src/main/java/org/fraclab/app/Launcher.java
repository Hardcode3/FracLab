package org.fraclab.app;

import javafx.application.Application;

/**
 * Entry point. It is a separate class from {@link FracLabApp} because a main class extending
 * {@link Application} fails to start with "JavaFX runtime components are missing" when run from the class path.
 */
public final class Launcher {
    private Launcher() { }

    public static void main(String[] args) {
        Application.launch(FracLabApp.class, args);
    }
}
