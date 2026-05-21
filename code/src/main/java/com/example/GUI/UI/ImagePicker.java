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

public class ImagePicker {

    private static final Logger log =
            LogManager.getLogger(ImagePicker.class);

    /**
     * NOW: File + BufferedImage instead of String + BufferedImage
     */
    private final Observable<Pair<File, BufferedImage>> observable =
            new Observable<>();

    public void subscribe(java.util.function.Consumer<Pair<File, BufferedImage>> p) {
        observable.subscribe(p);
    }

    public void showUI() {

        log.debug(LOG_OPEN_FILE_CHOOSER);

        Path currentDirectory =
                FilePickerUtils.resolveInitialDirectory();

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(currentDirectory.toFile());
        fileChooser.setPreferredSize(
                new Dimension(DIALOG_WIDTH_FILE_CHOOSER, DIALOG_HEIGHT_FILE_CHOOSER)
        );

        int result = fileChooser.showOpenDialog(null);

        if (result != JFileChooser.APPROVE_OPTION) {
            log.debug(LOG_FILE_CHOOSER_CANCELLED);
            return;
        }

        File selectedFile = fileChooser.getSelectedFile();

        log.debug(LOG_FILE_SELECTED, selectedFile.getAbsolutePath());

        handleImageSelectionAsync(selectedFile);
    }

    private void handleImageSelectionAsync(File file) {

        new SwingWorker<Pair<File, BufferedImage>, Void>() {

            @Override
            protected Pair<File, BufferedImage> doInBackground() throws Exception {

                log.debug(LOG_READING_IMAGE, file.getAbsolutePath());

                BufferedImage image = ImageIO.read(file);

                if (image == null) {
                    return null;
                }

                log.debug(LOG_IMAGE_LOADED, image.getWidth(), image.getHeight());

                FilePickerUtils.copyToOutputDirectory(file);
                return new Pair<>(file, image);
            }

            @Override
            protected void done() {

                try {

                    Pair<File, BufferedImage> result = get();

                    if (result == null) {
                        log.warn(LOG_UNREADABLE_IMAGE, file.getName());
                        return;
                    }

                    observable.set(result);

                    log.info(LOG_IMAGE_PUBLISHED, file.getName());

                } catch (InterruptedException e) {

                    Thread.currentThread().interrupt();

                    log.error(
                            LOG_IMAGE_READ_FAILED,
                            file.getAbsolutePath(),
                            e.getMessage(),
                            e
                    );

                } catch (ExecutionException e) {

                    Throwable cause =
                            e.getCause() != null
                                    ? e.getCause()
                                    : e;

                    log.error(
                            LOG_IMAGE_READ_FAILED,
                            file.getAbsolutePath(),
                            cause.getMessage(),
                            cause
                    );
                }
            }

        }.execute();
    }
}