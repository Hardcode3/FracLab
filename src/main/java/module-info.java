module org.jfractals {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;
    requires commons.math3;
    requires java.desktop;

    opens org.jfractals.ui to javafx.fxml;

    exports org.jfractals.app;
    exports org.jfractals.ui;
    exports org.jfractals.ui.overlay;
    exports org.jfractals.model;
    exports org.jfractals.ui.view;
    opens org.jfractals.ui.view to javafx.fxml;
    exports org.jfractals.ui.export;
    opens org.jfractals.ui.export to javafx.fxml;
    exports org.jfractals.ui.settings;
    opens org.jfractals.ui.settings to javafx.fxml;
}
