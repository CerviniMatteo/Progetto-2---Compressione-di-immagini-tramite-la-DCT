package com.example.UI;

import com.example.lib.utils.Observable;
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

import static com.example.lib.constants.PickerConstants.*;
import static com.example.lib.utils.ImageUtils.saveAsBMP;

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
 * @see com.example.lib.utils.Observable
 */
public class ImagePicker {

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
     *   <li>Resolve the user's home directory using {@code System.getProperty(HOME_PATH)}.</li>
     *   <li>Try to locate the downloads folder using {@code DOWNLOAD_PATH}; if it doesn't exist,
     *       fall back to {@code SCARICATI_PATH} (localized name).</li>
     *   <li>Resolve an application-specific path by appending {@code PROJECT_DIR_PATH} and {@code IMMAGINI}.</li>
     *   <li>Create a {@link JFileChooser} and set its current directory to the resolved path (or downloads if the
     *       resolved path doesn't exist).</li>
     *   <li>Display the file chooser. If the user approves a selection, attempt to read the file with
     *       {@link ImageIO#read(File)}.</li>
     *   <li>If the image is successfully read (non-null), publish a {@link org.apache.commons.math3.util.Pair}
     *       containing the file name and image via the internal {@link #observable}.</li>
     *   <li>If an {@link IOException} occurs while reading, print the stack trace to aid debugging.</li>
     * </ol>
     * </p>
     *
     * <p>
     * Notes:
     * - If the user cancels the dialog or {@link ImageIO#read(File)} returns {@code null} (file is not a readable
     *   image), no notification is sent to subscribers.
     * - The method sets a large file chooser preferred size; depending on platform/look-and-feel this may be ignored.
     * </p>
     *
     * @see java.lang.System#getProperty(String)
     * @see javax.swing.JFileChooser
     * @see javax.imageio.ImageIO
     */
    public void showUI() {
        String home = System.getProperty(HOME_PATH);

        Path downloads = Paths.get(home, DOWNLOAD_PATH);

        if (!Files.exists(downloads)) {
            downloads = Paths.get(home, SCARICATI_PATH);
        }

        Path fullPath = downloads.resolve(
                PROJECT_DIR_PATH
        ).resolve(IMMAGINI);


        JFileChooser fc = new JFileChooser();
        if (Files.exists(fullPath)) {
            fc.setCurrentDirectory(fullPath.toFile());
        } else {
            fc.setCurrentDirectory(downloads.toFile());
        }

        fc.setSize(new Dimension(1240, 1240));
        int result = fc.showOpenDialog(null);

        if (result != JFileChooser.APPROVE_OPTION) return;

        File file = fc.getSelectedFile();

        try {
            BufferedImage img = ImageIO.read(file);

            if (img == null) return;
            saveAsBMP(img, "output/" + file.getName().substring(0, file.getName().lastIndexOf('.')));
            observable.set(new Pair<>(file.getName(), img));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}