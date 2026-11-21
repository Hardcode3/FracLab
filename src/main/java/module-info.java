module org.fraclab {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;
    requires java.desktop;

    opens org.fraclab.ui.export to javafx.fxml;
    opens org.fraclab.ui.settings to javafx.fxml;
    opens org.fraclab.ui.view to javafx.fxml;
    opens org.fraclab.ui to javafx.fxml, javafx.graphics;
    opens org.fraclab.app to javafx.fxml, javafx.graphics;
    opens org.fraclab.ui.overlay to javafx.fxml, javafx.graphics;
}
