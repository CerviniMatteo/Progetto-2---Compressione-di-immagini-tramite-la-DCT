package com.example.GUI.UI;

import com.example.GUI.observer.Observable;
import com.example.GUI.utils.FilePickerUtils;
import org.apache.commons.math3.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;

import static com.example.GUI.constants.PickerConstants.*;
import static com.example.GUI.constants.UIStyleConstants.*;

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
 * </p>
 * <ul>
 *   <li>{@code getFirst()} is the file name (String)</li>
 *   <li>{@code getSecond()} is the {@link BufferedImage}</li>
 * </ul>
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


    /**
     * Logger for image picker events and errors.
     */
    private static final Logger log = LogManager.getLogger(ImagePicker.class);

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
     * <p>Behavior and steps:</p>
     * <ol>
     *   <li>Resolve the user's home directory and downloads folder</li>
     *   <li>Resolve the application-specific image directory</li>
     *   <li>Display the file chooser</li>
     *   <li>Read the selected image and copy to output directory</li>
     *   <li>Publish to subscribers</li>
     * </ol>
     *
     * @see java.lang.System#getProperty(String)
     * @see javax.swing.JFileChooser
     * @see javax.imageio.ImageIO
     */
     public void showUI() {
         log.debug(LOG_OPEN_FILE_CHOOSER);
         Path currentDirectory = FilePickerUtils.resolveInitialDirectory();

         JFileChooser fileChooser = new JFileChooser();
         fileChooser.setCurrentDirectory(currentDirectory.toFile());
         fileChooser.setPreferredSize(new Dimension(DIALOG_WIDTH_FILE_CHOOSER, DIALOG_HEIGHT_FILE_CHOOSER));

         int result = fileChooser.showOpenDialog(null);

        if (result != JFileChooser.APPROVE_OPTION) {
            log.debug(LOG_FILE_CHOOSER_CANCELLED);
            return;
        }

        File selectedFile = fileChooser.getSelectedFile();
        log.debug(LOG_FILE_SELECTED, selectedFile.getAbsolutePath());
        handleImageSelectionAsync(selectedFile);
    }

    /**
     * Handles the selected image: reads, validates, copies to output, and publishes.
     *
     * @param file the selected image file
     */
    private void handleImageSelectionAsync(File file) {
        new SwingWorker<Pair<String, BufferedImage>, Void>() {

            @Override
            protected Pair<String, BufferedImage> doInBackground() throws Exception {
                log.debug(LOG_READING_IMAGE, file.getAbsolutePath());
                BufferedImage image = ImageIO.read(file);

                if (image == null) {
                    return null;
                }

                log.debug(LOG_IMAGE_LOADED, image.getWidth(), image.getHeight());
                FilePickerUtils.copyToOutputDirectory(file);
                return new Pair<>(file.getName(), image);
            }

            @Override
            protected void done() {
                try {
                    Pair<String, BufferedImage> result = get();

                    if (result == null) {
                        log.warn(LOG_UNREADABLE_IMAGE, file.getName());
                        return;
                    }

                    observable.set(result);
                    log.info(LOG_IMAGE_PUBLISHED, file.getName());

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.error(LOG_IMAGE_READ_FAILED, file.getAbsolutePath(), e.getMessage(), e);
                } catch (ExecutionException e) {
                    Throwable cause = e.getCause() != null ? e.getCause() : e;
                    log.error(LOG_IMAGE_READ_FAILED, file.getAbsolutePath(), cause.getMessage(), cause);
                }
            }
        }.execute();
    }

}