package org.fraclab.ui.export;

import javafx.application.Platform;
import javafx.scene.image.WritableImage;
import org.fraclab.render.FractalRenderService;
import org.fraclab.render.FractalRenderer;

import javafx.embed.swing.SwingFXUtils;
import org.fraclab.ui.view.FractalViewState;
import org.fraclab.ui.view.Viewport;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.function.Consumer;

public class FractalExportService {
    private final FractalRenderService renderService = new FractalRenderService(new FractalRenderer());

    public void export(FractalViewState renderState, FractalExportSettings exportSettings, Runnable onRendered) {
        Viewport vp = renderState.viewport();
        Consumer<WritableImage> postRender = image -> {
            BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
            String imageExtension = exportSettings.getImageExtension().toString().toLowerCase();
            if (!Arrays.asList(ImageIO.getWriterFormatNames()).contains(imageExtension)) {
                throw new RuntimeException(
                    "Invalid image extension " + imageExtension + " is not supported by the image writer backend. " +
                    "Compatible extensions are: " + Arrays.asList(ImageIO.getWriterFormatNames())
                );
            }
            File exportFile = exportSettings.getFpath();
            try {
                boolean written = ImageIO.write(bufferedImage, imageExtension, exportFile);
                if (!written) {
                    throw new IOException("No writer could encode this image as " + imageExtension);
                }
            } catch (java.io.IOException e) {
                throw new RuntimeException("Could not save fractal image " + exportFile + " to disk", e);
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
