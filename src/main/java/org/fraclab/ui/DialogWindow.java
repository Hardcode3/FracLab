package org.fraclab.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * A non-resizable secondary window whose content is loaded from FXML each time it opens.
 * At most one instance is open at a time: showing it again brings the open one to the front.
 * <p>
 * The controller is kept referenced while the window is open, so that whatever it holds
 * (for instance properties used in bidirectional bindings, which are weakly referenced) lives as long as the window.
 *
 * @param <C> Type of the FXML controller.
 */
public final class DialogWindow<C> implements AutoCloseable {
    private final URL fxml;
    private final String title;

    private Stage stage;
    @SuppressWarnings({"unused", "FieldCanBeLocal"}) // Only keeps the controller alive, see the class documentation.
    private C controller;

    public DialogWindow(URL fxml, String title) {
        this.fxml = Objects.requireNonNull(fxml, "fxml");
        this.title = Objects.requireNonNull(title, "title");
    }

    /** Same as {@link #show(Window, Consumer, Consumer)} without a callback once shown. */
    public void show(Window owner, Consumer<? super C> setUp) {
        show(owner, setUp, _ -> { });
    }

    /**
     * Opens the window, or brings it to the front if it is already open.
     *
     * @param owner  The window this one stays in front of.
     * @param setUp  Called with the freshly loaded controller, before the window is shown.
     * @param onShown Called with the controller once the window is shown.
     */
    public void show(Window owner, Consumer<? super C> setUp, Consumer<? super C> onShown) {
        if (stage != null) {
            stage.toFront();
            return;
        }
        FXMLLoader loader = new FXMLLoader(fxml);
        Parent content;
        try {
            content = loader.load();
        } catch (IOException e) {
            throw new UncheckedIOException("Could not load " + fxml, e);
        }
        C loadedController = loader.getController();
        setUp.accept(loadedController);

        Stage newStage = new Stage();
        newStage.initOwner(owner);
        newStage.setTitle(title);
        newStage.setResizable(false);
        newStage.setScene(new Scene(content));
        newStage.setOnShown(_ -> onShown.accept(loadedController));
        newStage.setOnHidden(_ -> {
            stage = null;
            controller = null;
        });

        stage = newStage;
        controller = loadedController;
        newStage.show();
    }

    @Override
    public void close() {
        if (stage != null) {
            stage.close();
        }
    }
}
