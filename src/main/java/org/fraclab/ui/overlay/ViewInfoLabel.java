package org.fraclab.ui.overlay;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.Label;
import org.fraclab.ui.view.Viewport;

public final class ViewInfoLabel extends Label {
    final private BooleanProperty showZoomProperty = new SimpleBooleanProperty(this, "showZoom", true);
    final private BooleanProperty showCenterCoordinatesProperty = new SimpleBooleanProperty(this, "showCenterCoordinates", true);

    private Viewport currenViewport;

    public ViewInfoLabel() {
        getStyleClass().addAll("overlay-label", "view-info");
        setMouseTransparent(true);

        showCenterCoordinatesProperty.addListener(_ -> refreshText());
        showZoomProperty.addListener(_ -> refreshText());
    }

    public void update(Viewport viewport) {
        currenViewport = viewport;
        refreshText();
    }

    public BooleanProperty getShowZoomProperty() {
        return showZoomProperty;
    }
    public BooleanProperty getShowCenterCoordinatesProperty() {
        return showCenterCoordinatesProperty;
    }

    private void refreshText() {
        if (currenViewport == null) return;

        StringBuilder labelText = new StringBuilder();
        if (showZoomProperty.get()) {
            labelText.append(String.format("Zoom: %.2fx \t", currenViewport.zoomFactor()));
        }
        if (showCenterCoordinatesProperty.get()) {
            labelText.append(String.format("Center: %.6f %+.6fi", currenViewport.centerXCp(), currenViewport.centerYCp()));
        }
        setText(labelText.toString());
    }
}
