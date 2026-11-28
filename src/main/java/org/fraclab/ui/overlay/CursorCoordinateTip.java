package org.fraclab.ui.overlay;

import javafx.scene.control.Label;
import org.fraclab.model.Viewport;

/** Shows the complex coordinates of the point under the mouse cursor, next to it. */
public final class CursorCoordinateTip extends Label {
    private static final double OFFSET_X_PX = 10;
    private static final double OFFSET_Y_PX = 20;

    public CursorCoordinateTip() {
        getStyleClass().addAll("overlay-label", "cursor-tip");
        setMouseTransparent(true);
        setVisible(false);
    }

    public void showAt(double mouseXPx, double mouseYPx, Viewport viewport) {
        setText("(" + OverlayFormat.complex(viewport.toComplexX(mouseXPx), viewport.toComplexY(mouseYPx)) + ")");
        setTranslateX(mouseXPx + OFFSET_X_PX);
        setTranslateY(mouseYPx + OFFSET_Y_PX);
        setVisible(true);
    }

    public void hide() {
        setVisible(false);
    }
}
