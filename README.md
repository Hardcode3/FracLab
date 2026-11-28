# FracLab

A JavaFX application for exploring Mandelbrot and Julia sets.

## Run

Install JDK 25 or newer and Maven, then run:

```sh
mvn javafx:run
```

You can drag to pan, scroll to zoom, press **R** to reset the view, and press **S** to open settings.

## Package the application

Install JDK 25 or newer and Maven, then run:

```sh
mvn -Pdist clean package
```

This builds two downloads in `target/dist/` for the operating system and processor of the machine running the build. Both include a trimmed Java runtime, so users do not need to install Java.

| Platform | Standalone (no installation) | Installer |
| --- | --- | --- |
| macOS | `FracLab-<version>-macos-<arch>.zip`, containing `FracLab.app` | `FracLab-<version>.dmg` |
| Windows | `FracLab-<version>-windows-<arch>.zip`, containing `FracLab\FracLab.exe` | `FracLab-<version>.msi` |
| Linux | `FracLab-<version>-linux-<arch>.tar.gz`, containing `FracLab/bin/FracLab` | `fraclab_<version>_<arch>.deb` |

The packages use the version in `pom.xml` without its qualifier, so `1.0-SNAPSHOT` becomes `1.0.0`. Platform-specific packaging options and icons are in `src/packaging/<os>/`.

Some installers need extra tools:

- **Windows:** the [WiX Toolset](https://wixtoolset.org) 5, installed with `dotnet tool install --global wix --version 5.0.2` and then `wix extension add --global WixToolset.UI.wixext/5.0.2 WixToolset.Util.wixext/5.0.2`.
- **Linux:** `fakeroot` for `.deb` packages. To build an `.rpm` package instead, install `rpm` and add `-Dinstaller.type=rpm`.
- **macOS:** nothing. Add `-Dinstaller.type=pkg` to build a `.pkg` installer instead of a disk image.

The macOS and Windows packages are not code-signed. On first launch, macOS users must Control-click the app and choose **Open**, and Windows SmartScreen asks users to confirm.

The value of `--win-upgrade-uuid` in `src/packaging/windows/installer.args` must never change. Windows uses it to recognize an installed FracLab and replace it with a newer version.

### Build for every platform

Open the repository's **Actions → Package app** workflow and choose **Run workflow**. It builds the packages for Windows x64, Linux x64 (including an `.rpm`), and Intel and Apple Silicon Macs. Download them from the `fraclab-*` artifacts of the completed run.

Pushing a tag such as `v1.2.0` runs the same builds with the version set to `1.2.0`, then creates a draft GitHub release with every package attached. Review the draft and publish it from the repository's **Releases** page.

## Project layout

| Package | Responsibility |
| --- | --- |
| `org.fraclab.app` | Starts JavaFX and creates the main window. |
| `org.fraclab.model` | Fractal settings, immutable render configuration, and the viewport mapping screen pixels to the complex plane. |
| `org.fraclab.fractal` | Mandelbrot and Julia escape-time calculations. |
| `org.fraclab.render` | Pixel calculation and background render scheduling. |
| `org.fraclab.export` | Rendering fractals to image files in the background. |
| `org.fraclab.ui` | Main window controller and helpers shared by the dialogs. |
| `org.fraclab.ui.*` | One package per part of the interface: the fractal `view` and its `overlay`s, and the `settings`, `navigation`, `export` and `about` dialogs. Their FXML and CSS files sit in the same package under `src/main/resources`. |

The settings form and renderer share a `FractalSettings` model. Each render receives an immutable `RenderRequest`, made of a `Viewport` and a `RenderSettings` snapshot, so background work does not read live JavaFX properties.

The complex plane follows the mathematical convention: the imaginary axis points up.

## License

FracLab is released under the [MIT License](LICENSE).
