package org.fraclab.ui.overlay;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

/** Which overlays are shown over the fractal, as chosen in the View menu. */
public final class OverlayOptions {
    private final BooleanProperty showScale = new SimpleBooleanProperty(this, "showScale", true);
    private final BooleanProperty showInfoLabel = new SimpleBooleanProperty(this, "showInfoLabel", true);
    private final BooleanProperty showCenterCoordinates = new SimpleBooleanProperty(this, "showCenterCoordinates", true);
    private final BooleanProperty showZoom = new SimpleBooleanProperty(this, "showZoom", true);
    private final BooleanProperty showAxes = new SimpleBooleanProperty(this, "showAxes", true);

    public BooleanProperty showScaleProperty() {
        return showScale;
    }

    public BooleanProperty showInfoLabelProperty() {
        return showInfoLabel;
    }

    public BooleanProperty showCenterCoordinatesProperty() {
        return showCenterCoordinates;
    }

    public BooleanProperty showZoomProperty() {
        return showZoom;
    }

    public BooleanProperty showAxesProperty() {
        return showAxes;
    }
}
