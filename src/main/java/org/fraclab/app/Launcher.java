package org.fraclab.app;

import javafx.application.Application;
import org.fraclab.ui.MainWindow;

public final class Launcher {
    private Launcher() { }

    static void main(String[] args) {
        Application.launch(MainWindow.class, args);
    }
}
