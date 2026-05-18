package com.example.GUI.utils;

import com.example.GUI.constants.GUIConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static com.example.GUI.constants.PickerConstants.*;

/**
 * Utility class for file picker and directory resolution operations.
 * <p>
 * This class provides reusable methods for:
 * <ul>
 *   <li>Resolving initial directories for file choosers</li>
 *   <li>Copying files to output directories</li>
 *   <li>File system operations with proper logging and error handling</li>
 * </ul>
 * </p>
 */
public class FilePickerUtils {

    private static final Logger log = LogManager.getLogger(FilePickerUtils.class);

    /**
     * Resolves the initial directory for the file chooser.
     * <p>
     * Attempts to locate the user's application-specific images directory by:
     * <ol>
     *   <li>Resolving the user's home directory</li>
     *   <li>Finding the Downloads folder (with fallback for Italian systems)</li>
     *   <li>Navigating to the project-specific images subdirectory</li>
     *   <li>Falling back to Downloads if the project directory doesn't exist</li>
     * </ol>
     * </p>
     * <p>
     * This method handles localization by supporting both English ("Downloads") and
     * Italian ("Scaricati") directory names.
     * </p>
     *
     * @return the resolved directory path for file chooser operations
     *
     * @see java.lang.System#getProperty
     * @see java.nio.file.Files#exists
     */
    public static Path resolveInitialDirectory() {
        log.debug(LOG_RESOLVE_INITIAL_DIR);
        String home = System.getProperty(HOME_PATH);
        Path downloads = Paths.get(home, DOWNLOAD_PATH);

        if (!Files.exists(downloads)) {
            log.debug(String.format(LOG_DOWNLOADS_NOT_FOUND, downloads));
            downloads = Paths.get(home, SCARICATI_PATH);
        }

        Path appImagesDir = downloads.resolve(PROJECT_DIR_PATH).resolve(IMMAGINI);
        Path resolvedDir = Files.exists(appImagesDir) ? appImagesDir : downloads;

        log.debug(String.format(LOG_USING_DIRECTORY, resolvedDir));
        return resolvedDir;
    }

    /**
     * Copies the selected file to the application's output directory.
     * <p>
     * This method:
     * <ol>
     *   <li>Logs the copy operation</li>
     *   <li>Creates the output directory if it doesn't exist</li>
     *   <li>Copies the file to the output directory, replacing if it already exists</li>
     *   <li>Logs the completion of the copy</li>
     * </ol>
     * </p>
     *
     * @param file the file to copy to the output directory
     *
     * @throws IOException if the copy operation fails
     *
     * @see java.nio.file.Files#createDirectories
     * @see java.nio.file.Files#copy
     * @see java.nio.file.StandardCopyOption#REPLACE_EXISTING
     */
    public static void copyToOutputDirectory(File file) throws IOException {
        log.debug(String.format(LOG_COPYING_TO_OUTPUT, file.getName()));
        Path outputDir = Paths.get(GUIConstants.OUTPUT_DIR_NAME);

        if (!Files.exists(outputDir)) {
            log.debug(LOG_CREATING_OUTPUT_DIR);
            Files.createDirectories(outputDir);
        }

        Path target = outputDir.resolve(file.getName());
        Files.copy(file.toPath(), target, StandardCopyOption.REPLACE_EXISTING);
        log.debug(String.format(LOG_FILE_COPIED, target.toAbsolutePath()));
    }


    /**
     * Extracts the base filename without extension.
     *
     * @param filenameWithExtension full filename (e.g., "image.bmp")
     * @return filename without extension (e.g., "image")
     */
    public static String extractFilename(String filenameWithExtension) {
        int dotIndex = filenameWithExtension.lastIndexOf('.');
        return dotIndex > 0 ? filenameWithExtension.substring(0, dotIndex) : filenameWithExtension;
    }
}

