# FracLab

A JavaFX application for exploring Mandelbrot and Julia sets.

## Run

Install JDK 25 or newer and Maven, then run:

```sh
mvn javafx:run
```

You can drag to pan, scroll to zoom, press **R** to reset the view, and press **S** to open settings.

## Build a standalone application

Install JDK 25 or newer and Maven. Build a self-contained application image for the current operating system with:

```sh
mvn clean javafx:jlink
```

The runnable image is created in `target/fraclab/`, and the same image is zipped as `target/fraclab.zip`. It includes a Java runtime, so users do not need to install Java separately. This command builds only for the operating system and architecture on which it runs.

To build bundles for Windows x64, Linux x64, and both Intel and Apple Silicon Macs, open the repository's **Actions → Package app** workflow and choose **Run workflow**. Download the matching `fraclab-*` artifact from the completed run. Tagging a commit with a name such as `v1.0.0` also starts these builds.

## Project layout

| Package | Responsibility |
| --- | --- |
| `org.fraclab.app` | Starts JavaFX and creates the main window. |
| `org.fraclab.ui` | FXML views and controllers for user interaction. |
| `org.fraclab.model` | Fractal settings and immutable render configuration. |
| `org.fraclab.fractal` | Mandelbrot and Julia escape-time calculations. |
| `org.fraclab.render` | Pixel calculation and background render scheduling. |

The settings form and renderer share a `FractalSettings` model. Each render receives an immutable `RenderSettings` snapshot so background work does not read live JavaFX properties.
