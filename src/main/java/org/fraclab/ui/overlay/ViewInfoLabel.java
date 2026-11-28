package org.fraclab.ui.overlay;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.Label;
import org.fraclab.model.Viewport;

/** Shows the zoom factor and the center of the view, each of which can be hidden. */
public final class ViewInfoLabel extends Label {
    private final BooleanProperty showZoom = new SimpleBooleanProperty(this, "showZoom", true);
    private final BooleanProperty showCenterCoordinates = new SimpleBooleanProperty(this, "showCenterCoordinates", true);

    private Viewport currentViewport;

    public ViewInfoLabel() {
        getStyleClass().addAll("overlay-label", "view-info");
        setMouseTransparent(true);

        showCenterCoordinates.addListener(_ -> refreshText());
        showZoom.addListener(_ -> refreshText());
    }

    public void update(Viewport viewport) {
        currentViewport = viewport;
        refreshText();
    }

    public BooleanProperty showZoomProperty() {
        return showZoom;
    }

    public BooleanProperty showCenterCoordinatesProperty() {
        return showCenterCoordinates;
    }

    private void refreshText() {
        if (currentViewport == null) {
            return;
        }
        StringBuilder labelText = new StringBuilder();
        if (showZoom.get()) {
            labelText.append(OverlayFormat.format("Zoom: %.2fx \t", currentViewport.zoomFactor()));
        }
        if (showCenterCoordinates.get()) {
            labelText.append("Center: ")
                    .append(OverlayFormat.complex(currentViewport.centerXCp(), currentViewport.centerYCp()));
        }
        setText(labelText.toString());
    }
}
