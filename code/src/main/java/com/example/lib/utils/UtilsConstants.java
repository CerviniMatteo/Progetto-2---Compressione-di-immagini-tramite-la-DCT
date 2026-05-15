// src/main/java/com/example/lib/utils/UtilsConstants.java
package com.example.lib.utils;

/**
 * Constants shared by utility classes in the library layer.
 * <p>
 * These values are used for image persistence, output folder naming,
 * and common log messages related to saving images.
 * </p>
 */
public class UtilsConstants {

    /**
     * File format name for bitmap images.
     */
    public static final String BMP = "bmp";

    /**
     * Bitmap file extension, including the leading dot.
     */
    public static final String DOT_BMP = ".bmp";

    /**
     * Log message prefix used when an image is saved successfully.
     */
    public static final String SAVE_IMAGE_TO = "Image saved to: ";

    /**
     * Log message used when image saving fails.
     */
    public static final String SAVING_ERROR = "Failed to save image";

    /**
     * Default relative output folder used by utility methods.
     */
    public static final String OUTPUT_PATH = "output/";
}