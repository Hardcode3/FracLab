package org.jfractals.ui.export;

import javafx.application.Platform;
import javafx.scene.image.WritableImage;
import org.jfractals.render.FractalRenderService;
import org.jfractals.render.FractalRenderer;

import javafx.embed.swing.SwingFXUtils;
import org.jfractals.ui.view.FractalViewState;
import org.jfractals.ui.view.Viewport;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.function.Consumer;

public class FractalExportService {
    private final FractalRenderService renderService = new FractalRenderService(new FractalRenderer());

    public void export(FractalViewState renderState, FractalExportSettings exportSettings, Runnable onRendered) {
        Viewport vp = renderState.viewport();
        Consumer<WritableImage> postRender = image -> {
            BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
            String imageExtension = exportSettings.getImageExtension().toString().toLowerCase();
            File exportFile = exportSettings.getFpath();
            try {
                ImageIO.write(bufferedImage, imageExtension, exportFile);
            } catch (java.io.IOException e) {
                throw new RuntimeException("Could not save fractal image " + exportFile.toString() + " to disk", e);
            }
            if (onRendered != null) {
                Platform.runLater(onRendered);
            }
        };
        renderService.render(
                exportSettings.getWidthPx(), exportSettings.getHeightPx(),
                vp.heightCp(),
                vp.centerXCp(), vp.centerYCp(),
                renderState.settings(),
                postRender
        );
    }
}
