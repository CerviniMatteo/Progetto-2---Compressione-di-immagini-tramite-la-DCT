package com.example.GUI.constants;

/**
 * Constants used by the GUI picker dialogs and file selection utilities.
 * <p>This class contains:</p>
 * <ul>
 *   <li>system property keys and common directory names</li>
 *   <li>dialog button labels and validation messages</li>
 *   <li>log templates for the integer input picker and image/file chooser</li>
 * </ul>
 */
public class PickerConstants {

    /**
     * System property key used to resolve the user's home directory.
     */
    public static final String HOME_PATH = "user.home";

    /**
     * Standard downloads folder name on English-language systems.
     */
    public static final String DOWNLOAD_PATH = "Downloads";

    /**
     * Standard downloads folder name on Italian-language systems.
     */
    public static final String SCARICATI_PATH = "Scaricati";

    /**
     * Project-specific directory name used while resolving file paths.
     */
    public static final String PROJECT_DIR_PATH = "Materiale per Progetti Esame (secondo progetto)-20260511";

    /**
     * Directory name used when working with image collections.
     */
    public static final String IMMAGINI = "immagini";

    /**
     * Text shown on submit buttons in picker dialogs.
     */
    public static final String SUBMIT = "Submit";

    /**
     * Generic error dialog title.
     */
    public static final String ERROR = "Error";

    /**
     * Title for the compression factor picker window.
     */
    public static final String COMPRESSION_FACTOR_PICKER = "COMPRESSION FACTOR VALUES PICKER";

    /**
     * Label for the compression factor input.
     */
    public static final String F = "F";

    /**
     * Label for the truncation parameter input.
     */
    public static final String D = "d";

    /**
     * Validation error message for {@code F}.
     */
    public static final String F_POSITIVE_ERROR = F + " must be positive";

    /**
     * Validation error message for {@code d}.
     */
    public static final String D_VALUE_ERROR = D + " must be >= 0 and <= (2" + F + "- 2)";

    public static final String F_ROWS_COLS_ERROR = "F must be less than the number of rows and columns";

    /**
     * Error message shown when the user enters non-integer values.
     */
    public static final String INVALID_INTEGER_INPUT_ERROR = "Invalid input: Please enter integers only";

    /**
     * Log message printed when the integer picker is initialized.
     */
    public static final String LOG_INTEGER_PICKER_INITIALIZED = "Integer picker initialized";

    /**
     * Log message printed when the integer picker is displayed.
     */
    public static final String LOG_INTEGER_PICKER_SHOWN = "Integer picker shown";

    /**
     * Log message printed before parsing the picker inputs.
     */
    public static final String LOG_PARSE_INPUTS = "Attempting to parse inputs: F='%s', d='%s'";

    /**
     * Log message printed after parsing picker inputs successfully.
     */
    public static final String LOG_PARSED_INPUTS = "Parsed integers: F=%d, d=%d";

    /**
     * Log message printed after successful validation of picker inputs.
     */
    public static final String LOG_VALIDATION_SUCCESS = "Parameters validated successfully: F=%d, d=%d";

    /**
     * Prefix used when validation fails.
     */
    public static final String LOG_VALIDATION_FAILED_PREFIX = "Validation failed: ";

    /**
     * Log message printed when the file chooser dialog opens.
     */
    public static final String LOG_OPEN_FILE_CHOOSER = "Opening file chooser dialog";

    /**
     * Log message printed when the user cancels the file chooser.
     */
    public static final String LOG_FILE_CHOOSER_CANCELLED = "File chooser cancelled by user";

    /**
     * Log message printed when a file is selected.
     */
    public static final String LOG_FILE_SELECTED = "File selected: %s";

    /**
     * Log message printed when the application resolves the initial directory for the chooser.
     */
    public static final String LOG_RESOLVE_INITIAL_DIR = "Resolving initial file chooser directory";

    /**
     * Log message printed when the expected downloads directory cannot be found.
     * The placeholder represents the fallback path being tested.
     */
    public static final String LOG_DOWNLOADS_NOT_FOUND = "Downloads directory not found at %s, trying fallback";

    /**
     * Log message printed when a directory is chosen for file browsing.
     */
    public static final String LOG_USING_DIRECTORY = "Using directory: %s";

    /**
     * Log message printed when the application starts reading an image file.
     */
    public static final String LOG_READING_IMAGE = "Reading image from %s";

    /**
     * Log message printed when the selected file cannot be read as an image.
     */
    public static final String LOG_UNREADABLE_IMAGE = "File %s is not a readable image format";

    /**
     * Log message printed when an image is loaded successfully.
     */
    public static final String LOG_IMAGE_LOADED = "Image loaded successfully: %dx%d pixels";

    /**
     * Log message printed when a loaded image is published to observers.
     */
    public static final String LOG_IMAGE_PUBLISHED = "Image published: %s";

    /**
     * Prefix used when image loading fails.
     */
    public static final String LOG_IMAGE_READ_FAILED = "Failed to read image from %s: %s";

    /**
     * Log message printed when copying a file into the output directory.
     */
    public static final String LOG_COPYING_TO_OUTPUT = "Copying file to output directory: %s";

    /**
     * Log message printed when the output directory is created.
     */
    public static final String LOG_CREATING_OUTPUT_DIR = "Creating output directory";

    /**
     * Log message printed after a file has been copied successfully.
     */
    public static final String LOG_FILE_COPIED = "File copied to: %s";
}