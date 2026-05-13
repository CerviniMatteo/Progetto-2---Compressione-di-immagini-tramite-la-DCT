package com.example.GUI.constants;

public class PickerConstants {
    public final static String HOME_PATH = "user.home";
    public final static String DOWNLOAD_PATH = "Downloads";
    public final static String SCARICATI_PATH = "Scaricati";
    public final static String PROJECT_DIR_PATH = "Materiale per Progetti Esame (secondo progetto)-20260511";
    public final static String IMMAGINI = "immagini";
    public static final String SUBMIT = "Submit";
    public static final String ERROR = "Error";
    public static final String COMPRESSION_FACTOR_PICKER =  "COMPRESSION FACTOR VALUES PICKER";
    public final static String F = "F";
    public final static String D = "d";
    public final static String F_POSITIVE_ERROR = F + " must be positive";
    public final static String D_VALUE_ERROR = D + " must be >= 0 and <= (2" + F + "- 2)";

    public static final String INVALID_INTEGER_INPUT_ERROR = "Invalid input: Please enter integers only";

    public static final String LOG_INTEGER_PICKER_INITIALIZED = "Integer picker initialized";
    public static final String LOG_INTEGER_PICKER_SHOWN = "Integer picker shown";
    public static final String LOG_PARSE_INPUTS = "Attempting to parse inputs: F='%s', d='%s'";
    public static final String LOG_PARSED_INPUTS = "Parsed integers: F=%d, d=%d";
    public static final String LOG_VALIDATION_SUCCESS = "Parameters validated successfully: F=%d, d=%d";
    public static final String LOG_VALIDATION_FAILED_PREFIX = "Validation failed: ";

    public static final String LOG_OPEN_FILE_CHOOSER = "Opening file chooser dialog";
    public static final String LOG_FILE_CHOOSER_CANCELLED = "File chooser cancelled by user";
    public static final String LOG_FILE_SELECTED = "File selected: %s";
    public static final String LOG_RESOLVE_INITIAL_DIR = "Resolving initial file chooser directory";
    public static final String LOG_DOWNLOADS_NOT_FOUND = "Downloads directory not found at %s, trying fallback";
    public static final String LOG_USING_DIRECTORY = "Using directory: %s";
    public static final String LOG_READING_IMAGE = "Reading image from %s";
    public static final String LOG_UNREADABLE_IMAGE = "File %s is not a readable image format";
    public static final String LOG_IMAGE_LOADED = "Image loaded successfully: %dx%d pixels";
    public static final String LOG_IMAGE_PUBLISHED = "Image published: %s";
    public static final String LOG_IMAGE_READ_FAILED = "Failed to read image from %s: %s";
    public static final String LOG_COPYING_TO_OUTPUT = "Copying file to output directory: %s";
    public static final String LOG_CREATING_OUTPUT_DIR = "Creating output directory";
    public static final String LOG_FILE_COPIED = "File copied to: %s";
}
