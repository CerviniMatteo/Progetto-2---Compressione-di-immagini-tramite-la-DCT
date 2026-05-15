// src/main/java/com/example/GUI/constants/GUIConstants.java
package com.example.GUI.constants;

/**
 * Shared constants used across the GUI layer.
 * <p>
 * This class centralizes text labels, window titles, file naming conventions,
 * and log templates used by the image compression user interface.
 * </p>
 *
 * <p>
 * Keeping GUI text in one place makes the application easier to maintain and
 * helps keep labels consistent across windows and dialogs.
 * </p>
 */
public class GUIConstants {

    /**
     * Main application window title.
     */
    public static final String APP_TITLE = "DCT Image Compression Tool";

    /**
     * Title shown in the window where the user chooses which assignment part to run.
     */
    public static final String PART_CHOOSER_TITLE = "Choose what assignment part to be run";

    /**
     * Title shown in the image compression window.
     */
    public static final String DCT_IMAGE_COMPRESSION_TITLE = "DCT Image Compression";

    /**
     * Short description displayed in the part chooser window.
     */
    public static final String PART_CHOOSER_DESCRIPTION = "Select an operation to begin";

    /**
     * HTML label used for the Part 1 button.
     */
    public static final String PART1_BUTTON_HTML =
            "<html><div style='text-align:center;'>PART 1<br>Benchmark DCT</div></html>";

    /**
     * HTML label used for the Part 2 button.
     */
    public static final String PART2_BUTTON_HTML =
            "<html><div style='text-align:center;'>PART 2<br>GUI Image Compression</div></html>";

    /**
     * Title for the panel that contains compression parameters.
     */
    public static final String COMPRESSION_PARAMETERS_TITLE = "Compression Parameters";

    /**
     * Text shown on the button used to choose an image.
     */
    public static final String BUTTON_CHOOSE_IMAGE = "Choose Image";

    /**
     * Text shown on the button used to start image compression.
     */
    public static final String BUTTON_COMPRESS_IMAGE = "Compress Image";

    /**
     * Label used for the original image preview.
     */
    public static final String LABEL_ORIGINAL = "Original";

    /**
     * Label used for the compressed image preview.
     */
    public static final String LABEL_COMPRESSED = "Compressed";

    /**
     * Preferred font family for GUI components when Arial is available.
     */
    public static final String FONT_ARIAL = "Arial";

    /**
     * Generic fallback font family used by the UI.
     */
    public static final String FONT_SANS_SERIF = "SansSerif";

    /**
     * Suffix appended to output image names after compression.
     */
    public static final String COMPRESSED_SUFFIX = "_compressed";

    /**
     * Name of the directory where generated files are written.
     */
    public static final String OUTPUT_DIR_NAME = "output";

    /**
     * File extension used for bitmap images in this project.
     */
    public static final String FILE_EXTENSION_BMP = ".bmp";

    /**
     * HTML template used to display image metadata such as dimensions and file size.
     */
    public static final String IMAGE_METADATA_TEMPLATE = "<html>%d x %d pixel <br> %.2f kB</html>";

    /**
     * Log message printed when the image picker dialog is opened.
     */
    public static final String LOG_OPENING_IMAGE_PICKER = "Opening image picker dialog";

    /**
     * Log message printed after an image is selected.
     * Placeholders represent the file name and its dimensions.
     */
    public static final String LOG_IMAGE_SELECTED = "Image selected: %s (size: %dx%d pixels)";

    /**
     * Log message printed when compression is requested without a selected image.
     */
    public static final String LOG_COMPRESS_WITHOUT_IMAGE = "Compress action triggered but no image selected";

    /**
     * Log message printed when the compression parameter picker is opened.
     */
    public static final String LOG_OPENING_PARAMETERS_PICKER = "Opening compression parameters picker";

    /**
     * Log message printed when compression begins.
     * Placeholders represent the compression parameters.
     */
    public static final String LOG_COMPRESSION_START = "Compression started with parameters: F=%d, d=%d";

    /**
     * Log message printed when compression completes.
     * Placeholders represent the output file name and resulting image dimensions.
     */
    public static final String LOG_COMPRESSION_DONE = "Compression completed: %s (size: %dx%d pixels)";

    /**
     * Prefix used when logging a compression failure.
     */
    public static final String LOG_COMPRESSION_FAILED_PREFIX = "Compression failed: ";

    /**
     * Log message printed when the FlatDarkLaf theme cannot be applied.
     */
    public static final String LOG_THEME_APPLY_FAILED = "Failed to apply FlatDarkLaf look and feel";

    /**
     * Log message printed when Part 1 is selected from the chooser window.
     */
    public static final String LOG_PART1_SELECTED = "Part 1 (Benchmark) selected - launching DCT benchmark";

    /**
     * Log message printed when Part 2 is selected from the chooser window.
     */
    public static final String LOG_PART2_SELECTED = "Part 2 (Compression GUI) selected - launching image compression tool";

    /**
     * Log message printed when the benchmark background thread starts.
     */
    public static final String LOG_BENCHMARK_THREAD_START = "Benchmark thread started";

    /**
     * Log message printed when the benchmark background thread ends.
     */
    public static final String LOG_BENCHMARK_THREAD_DONE = "Benchmark thread completed";
}