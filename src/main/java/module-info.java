module org.fraclab {
    requires javafx.controls;
    requires javafx.fxml;
    // javax.imageio and java.awt.image, used to encode exported images.
    requires java.desktop;

    // JavaFX instantiates the Application subclass reflectively.
    opens org.fraclab.app to javafx.graphics;

    // FXMLLoader instantiates controllers and custom controls, and injects @FXML members.
    opens org.fraclab.ui to javafx.fxml;
    opens org.fraclab.ui.about to javafx.fxml;
    opens org.fraclab.ui.export to javafx.fxml;
    opens org.fraclab.ui.navigation to javafx.fxml;
    opens org.fraclab.ui.overlay to javafx.fxml;
    opens org.fraclab.ui.settings to javafx.fxml;
    opens org.fraclab.ui.view to javafx.fxml;
}
