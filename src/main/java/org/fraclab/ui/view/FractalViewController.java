package org.fraclab.ui.view;

import javafx.animation.PauseTransition;
import javafx.beans.InvalidationListener;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import org.fraclab.model.FractalSettings;
import org.fraclab.model.Viewport;
import org.fraclab.render.FractalRenderService;
import org.fraclab.render.FractalRenderer;
import org.fraclab.render.RenderRequest;
import org.fraclab.ui.overlay.AxisOverlay;
import org.fraclab.ui.overlay.CursorCoordinateTip;
import org.fraclab.ui.overlay.OverlayOptions;
import org.fraclab.ui.overlay.ScaleBar;
import org.fraclab.ui.overlay.ViewInfoLabel;

import java.util.List;

/**
 * Displays the fractal and its overlays, and lets the user pan by dragging and zoom by scrolling.
 * Owns the fractal settings, and renders again whenever they or the view change.
 */
public final class FractalViewController implements AutoCloseable {
    private static final double ZOOM_SPEED = 0.005;
    private static final Duration RENDER_DEBOUNCE = Duration.millis(150);

    private final FractalSettings settings = new FractalSettings();
    private final FractalRenderService renderService = new FractalRenderService(new FractalRenderer());
    private final PauseTransition renderDebounce = new PauseTransition(RENDER_DEBOUNCE);

    private Viewport viewport = Viewport.initial();
    private List<Node> panLayers;
    private double pressX;
    private double pressY;

    @FXML private StackPane fractalPane;
    @FXML private ImageView fractalImageView;
    @FXML private AxisOverlay axisOverlay;
    @FXML private ViewInfoLabel infoLabel;
    @FXML private ScaleBar scaleBar;
    @FXML private CursorCoordinateTip cursorTip;

    @FXML
    private void initialize() {
        panLayers = List.of(fractalImageView, axisOverlay);

        fractalImageView.fitWidthProperty().bind(fractalPane.widthProperty());
        fractalImageView.fitHeightProperty().bind(fractalPane.heightProperty());

        renderDebounce.setOnFinished(_ -> render());
        settings.addListener(_ -> scheduleRender());

        InvalidationListener onResize = _ -> {
            syncViewportSize();
            axisOverlay.update(viewport);
            scheduleRender();
        };
        fractalPane.widthProperty().addListener(onResize);
        fractalPane.heightProperty().addListener(onResize);

        fractalPane.setOnMouseMoved(e -> cursorTip.showAt(e.getX(), e.getY(), viewport));
        fractalPane.setOnMouseExited(_ -> cursorTip.hide());
        fractalPane.setOnMousePressed(e -> {
            pressX = e.getX();
            pressY = e.getY();
        });
        fractalPane.setOnMouseDragged(e -> setPanOffset(e.getX() - pressX, e.getY() - pressY));
        fractalPane.setOnMouseReleased(e -> finishPan(e.getX() - pressX, e.getY() - pressY));
        fractalPane.setOnScroll(e -> zoomAt(e.getX(), e.getY(), e.getDeltaY()));
    }

    /** Shows or hides each overlay according to the options. */
    public void bindOverlayOptions(OverlayOptions options) {
        scaleBar.visibleProperty().bind(options.showScaleProperty());
        // An info label showing nothing would still draw an empty box.
        infoLabel.visibleProperty().bind(options.showInfoLabelProperty()
                .and(options.showZoomProperty().or(options.showCenterCoordinatesProperty())));
        infoLabel.showCenterCoordinatesProperty().bind(options.showCenterCoordinatesProperty());
        infoLabel.showZoomProperty().bind(options.showZoomProperty());
        axisOverlay.visibleProperty().bind(options.showAxesProperty());
    }

    /** Renders the first image. Must be called once the view is laid out at its actual size. */
    public void start() {
        syncViewportSize();
        render();
    }

    /** The settings of the displayed fractal, which can be edited. */
    public FractalSettings settings() {
        return settings;
    }

    public Viewport viewport() {
        return viewport;
    }

    /** What is currently displayed, as a request to render it again elsewhere. */
    public RenderRequest renderRequest() {
        return new RenderRequest(viewport, settings.snapshot());
    }

    /**
     * Centers the view on a point of the complex plane at the given zoom factor.
     *
     * @throws IllegalArgumentException if the position or zoom factor are invalid.
     */
    public void goTo(double real, double imaginary, double zoomFactor) {
        syncViewportSize();
        viewport = viewport.centeredAt(real, imaginary, zoomFactor);
        resetPan();
        render();
    }

    public void resetView() {
        viewport = viewport.reset();
        resetPan();
        render();
    }

    private void finishPan(double dxPx, double dyPx) {
        if (dxPx == 0.0 && dyPx == 0.0) {
            resetPan();
            return;
        }
        viewport = viewport.panned(dxPx, dyPx);
        render();
    }

    private void zoomAt(double x, double y, double scrollDelta) {
        if (scrollDelta == 0.0) {
            return;
        }
        viewport = viewport.zoomedAt(x, y, Math.exp(scrollDelta * ZOOM_SPEED));
        render();
    }

    /** Moves the current image while dragging, until the panned view is rendered. */
    private void setPanOffset(double dxPx, double dyPx) {
        for (Node layer : panLayers) {
            layer.setTranslateX(dxPx);
            layer.setTranslateY(dyPx);
        }
    }

    private void resetPan() {
        setPanOffset(0, 0);
    }

    private void syncViewportSize() {
        viewport = viewport.withSize(fractalPane.getWidth(), fractalPane.getHeight());
    }

    private void scheduleRender() {
        renderDebounce.playFromStart();
    }

    private void render() {
        renderDebounce.stop();
        RenderRequest request = renderRequest();
        Viewport renderedViewport = request.viewport();
        infoLabel.update(renderedViewport);
        scaleBar.update(renderedViewport);
        renderService.render(request, image -> {
            fractalImageView.setImage(image);
            resetPan();
            axisOverlay.update(renderedViewport);
        });
    }

    @Override
    public void close() {
        renderDebounce.stop();
        renderService.close();
    }
}
