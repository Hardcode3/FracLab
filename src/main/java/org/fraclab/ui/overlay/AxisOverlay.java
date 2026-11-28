package org.fraclab.ui.overlay;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import org.fraclab.model.Viewport;

/** Draws the real and imaginary axes of the complex plane over the fractal. */
public final class AxisOverlay extends Pane {
    private static final Color AXIS_COLOR = Color.GRAY;
    private static final double AXIS_WIDTH = 1.5d;

    private final Line xAxis = newAxis();
    private final Line yAxis = newAxis();

    public AxisOverlay() {
        setMouseTransparent(true);
        getChildren().addAll(xAxis, yAxis);
    }

    public void update(Viewport viewport) {
        final double originXPx = viewport.toScreenX(0.0d);
        final double originYPx = viewport.toScreenY(0.0d);

        // The imaginary (vertical) axis is visible when the origin is horizontally within the view,
        // and the real (horizontal) axis when it is vertically within the view.
        final boolean yAxisIsInViewport = originXPx >= 0 && originXPx <= viewport.widthPx();
        final boolean xAxisIsInViewport = originYPx >= 0 && originYPx <= viewport.heightPx();

        yAxis.setVisible(yAxisIsInViewport);
        yAxis.setStartX(originXPx);
        yAxis.setStartY(0.0d);
        yAxis.setEndX(originXPx);
        yAxis.setEndY(viewport.heightPx());

        xAxis.setVisible(xAxisIsInViewport);
        xAxis.setStartX(0.0d);
        xAxis.setStartY(originYPx);
        xAxis.setEndX(viewport.widthPx());
        xAxis.setEndY(originYPx);
    }

    private static Line newAxis() {
        Line line = new Line();
        line.setStroke(AXIS_COLOR);
        line.setStrokeWidth(AXIS_WIDTH);

        // Axes are positioned manually, independently of the parent layout.
        // Therefore, their layout should not be recomputed when the parent's layout updates.
        line.setManaged(false);
        return line;
    }
}
