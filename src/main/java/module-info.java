module org.jfractals {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;
    requires java.desktop;

    opens org.jfractals.ui.export to javafx.fxml;
    opens org.jfractals.ui.settings to javafx.fxml;
    opens org.jfractals.ui.view to javafx.fxml;
    opens org.jfractals.ui to javafx.fxml, javafx.graphics;
    opens org.jfractals.app to javafx.fxml, javafx.graphics;
    opens org.jfractals.ui.overlay to javafx.fxml, javafx.graphics;
}
