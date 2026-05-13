package com.example.GUI.UI;

import com.example.GUI.observer.Observable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.math3.util.Pair;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static com.example.lib.constants.PickerConstants.*;

/**
 * A simple file/image picker UI that lets the user choose an image file and publishes
 * the selected image (filename and {@link BufferedImage}) to subscribers.
 * <p>
 * The picker attempts to open the user's download directory (with a fallback to an
 * alternative localized folder name). It resolves an application-specific subdirectory
 * (using {@code PROJECT_DIR_PATH} and {@code IMMAGINI}) and sets the file chooser's
 * current directory accordingly. The file chooser is shown modally; when the user approves
 * a file, the picker reads it via {@link ImageIO#read(File)} and, if successful, publishes
 * a {@link org.apache.commons.math3.util.Pair} where:
 * <ul>
 *   <li>{@code getFirst()} is the file name (String)</li>
 *   <li>{@code getSecond()} is the {@link BufferedImage}</li>
 * </ul>
 * </p>
 *
 * <p>
 * Errors reading the file are printed to standard error (stack trace). If the selected file
 * cannot be decoded as an image ({@link ImageIO#read(File)} returns {@code null}) the picker
 * returns silently without notifying subscribers.
 * </p>
 *
 * @see javax.imageio.ImageIO
 * @see javax.swing.JFileChooser
 * @see Observable
 */
public class ImagePicker {

    // ========================================================
    // CONSTANTS
    // ========================================================

    /** File chooser window width in pixels. */
    private static final int FILE_CHOOSER_WIDTH = 1240;

    /** File chooser window height in pixels. */
    private static final int FILE_CHOOSER_HEIGHT = 1240;

    /** Output directory name. */
    private static final String OUTPUT_DIR = "output";

    /**
     * Logger for image picker events and errors.
     */
    private static final Log log = LogFactory.getLog(ImagePicker.class);

    /**
     * Observable used to notify subscribers when an image is selected.
     * <p>
     * The observable publishes a {@link org.apache.commons.math3.util.Pair} whose
     * {@code getFirst()} contains the selected file name and {@code getSecond()}
     * contains the loaded {@link BufferedImage}.
     * </p>
     */
    private final Observable<Pair<String, BufferedImage>> observable =
            new Observable<>();

    /**
     * Subscribe a consumer that will be invoked when the user selects an image.
     *
     * @param p a {@code java.util.function.Consumer} that accepts a {@link org.apache.commons.math3.util.Pair}
     *          where the first element is the file name ({@link String}) and the second element is the
     *          {@link BufferedImage} loaded from the selected file.
     */
    public void subscribe(java.util.function.Consumer<Pair<String, BufferedImage>> p) {
        observable.subscribe(p);
    }

    /**
     * Show the file chooser UI and publish the selected image to subscribers.
     * <p>
     * Behavior and steps:
     * <ol>
     *   <li>Resolve the user's home directory and downloads folder</li>
     *   <li>Resolve the application-specific image directory</li>
     *   <li>Display the file chooser</li>
     *   <li>Read the selected image and copy to output directory</li>
     *   <li>Publish to subscribers</li>
     * </ol>
     * </p>
     *
     * @see java.lang.System#getProperty(String)
     * @see javax.swing.JFileChooser
     * @see javax.imageio.ImageIO
     */
    public void showUI() {
        log.debug("Opening file chooser dialog");
        Path currentDirectory = resolveInitialDirectory();

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(currentDirectory.toFile());
        fileChooser.setSize(new Dimension(FILE_CHOOSER_WIDTH, FILE_CHOOSER_HEIGHT));

        int result = fileChooser.showOpenDialog(null);

        if (result != JFileChooser.APPROVE_OPTION) {
            log.debug("File chooser cancelled by user");
            return;
        }

        File selectedFile = fileChooser.getSelectedFile();
        log.debug(String.format("File selected: %s", selectedFile.getAbsolutePath()));
        handleImageSelection(selectedFile);
    }

    /**
     * Resolves the initial directory for the file chooser.
     * <p>
     * Attempts to use the application-specific images directory, falling back to
     * the user's downloads directory if it doesn't exist.
     * </p>
     *
     * @return the resolved directory path
     */
    private Path resolveInitialDirectory() {
        log.debug("Resolving initial file chooser directory");
        String home = System.getProperty(HOME_PATH);
        Path downloads = Paths.get(home, DOWNLOAD_PATH);

        if (!Files.exists(downloads)) {
            log.debug(String.format("Downloads directory not found at %s, trying fallback", downloads));
            downloads = Paths.get(home, SCARICATI_PATH);
        }

        Path appImagesDir = downloads.resolve(PROJECT_DIR_PATH).resolve(IMMAGINI);
        Path resolvedDir = Files.exists(appImagesDir) ? appImagesDir : downloads;
        
        log.debug(String.format("Using directory: %s", resolvedDir));
        return resolvedDir;
    }

    /**
     * Handles the selected image: reads, validates, copies to output, and publishes.
     *
     * @param file the selected image file
     */
    private void handleImageSelection(File file) {
        try {
            log.debug(String.format("Reading image from %s", file.getAbsolutePath()));
            BufferedImage image = ImageIO.read(file);

            if (image == null) {
                log.warn(String.format("File %s is not a readable image format", file.getName()));
                return;
            }

            log.debug(String.format("Image loaded successfully: %dx%d pixels", image.getWidth(), image.getHeight()));

            copyToOutputDirectory(file);
            observable.set(new Pair<>(file.getName(), image));
            log.info(String.format("Image published: %s", file.getName()));

        } catch (IOException e) {
            log.error(String.format("Failed to read image from %s: %s", file.getAbsolutePath(), e.getMessage()), e);
        }
    }

    /**
     * Copies the selected file to the output directory.
     *
     * @param file the file to copy
     * @throws IOException if the copy operation fails
     */
    private void copyToOutputDirectory(File file) throws IOException {
        log.debug(String.format("Copying file to output directory: %s", file.getName()));
        Path outputDir = Paths.get(OUTPUT_DIR);

        if (!Files.exists(outputDir)) {
            log.debug("Creating output directory");
            Files.createDirectories(outputDir);
        }

        Path target = outputDir.resolve(file.getName());
        Files.copy(file.toPath(), target, StandardCopyOption.REPLACE_EXISTING);
        log.debug(String.format("File copied to: %s", target.toAbsolutePath()));
    }
}