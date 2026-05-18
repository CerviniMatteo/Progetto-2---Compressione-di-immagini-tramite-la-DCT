
package com.example.GUI.constants;

import java.awt.*;

/**
 * Centralized constants for UI styling including colors, dimensions, fonts, and layout parameters.
 * <p>
 * This class contains all magic numbers and hardcoded visual values used throughout the GUI,
 * making it easy to maintain a consistent look and feel across the application.
 * </p>
 */
public final class UIStyleConstants {

    // ========================================================
    // COLOR CONSTANTS (RGB)
    // ========================================================

    /** Dark background color used throughout the UI. */
    public static final Color COLOR_DARK = new Color(30, 30, 30);

    /** Medium dark background color for panels. */
    public static final Color COLOR_MEDIUM_DARK = new Color(45, 45, 45);

    /** Steel blue color for accents and borders. */
    public static final Color COLOR_STEELBLUE = new Color(70, 130, 180);

    /** Light gray text color for secondary text. */
    public static final Color COLOR_LIGHT_GRAY = new Color(120, 120, 120);

    /** Medium gray color used for borders. */
    public static final Color COLOR_BORDER_DARK = new Color(70, 70, 70);

    /** Gray color for placeholder or disabled text. */
    public static final Color COLOR_GRAY_PLACEHOLDER = new Color(150, 150, 150);

    /** Gray color for borders and dividers. */
    public static final Color COLOR_GRAY_BORDER = new Color(100, 100, 100);

    // ========================================================
    // WINDOW DIMENSIONS
    // ========================================================

    /** Compression Coefficients Picker window width in pixels. */
    public static final int WINDOW_WIDTH_PICKER = 500;

    /** Compression Coefficients Picker window height in pixels. */
    public static final int WINDOW_HEIGHT_PICKER = 300;

    /** File Chooser dialog width in pixels. */
    public static final int DIALOG_WIDTH_FILE_CHOOSER = 512;

    /** File Chooser dialog height in pixels. */
    public static final int DIALOG_HEIGHT_FILE_CHOOSER = 400;

    // ========================================================
    // BUTTON DIMENSIONS
    // ========================================================

    /** Standard button width in Part Chooser. */
    public static final int BUTTON_WIDTH_PART_CHOOSER = 250;

    /** Standard button height in Part Chooser. */
    public static final int BUTTON_HEIGHT_PART_CHOOSER = 60;

    /** Submit button width in pickers. */
    public static final int BUTTON_WIDTH_SUBMIT = 120;

    /** Submit button height in pickers. */
    public static final int BUTTON_HEIGHT_SUBMIT = 40;

    /** Font size for image preview labels. */
    public static final int FONT_SIZE_PREVIEW_LABEL = 20;

    /** Font size for image box titles. */
    public static final int FONT_SIZE_IMAGE_BOX_TITLE = 22;

    /** Font size for placeholder text in image boxes. */
    public static final int FONT_SIZE_PLACEHOLDER = 28;

    /** Font size for form labels. */
    public static final int FONT_SIZE_FORM_LABEL = 16;

    /** Font size for smaller labels and descriptions. */
    public static final int FONT_SIZE_SMALL = 14;

    // ========================================================
    // BORDER LAYOUT GAPS
    // ========================================================

    /** Standard horizontal gap in BorderLayout. */
    public static final int GAP_HORIZONTAL_STANDARD = 15;

    /** Standard vertical gap in BorderLayout. */
    public static final int GAP_VERTICAL_STANDARD = 10;

    /** Image preview container vertical gap. */
    public static final int GAP_VERTICAL_IMAGE = 15;

    /** Image panel vertical gap. */
    public static final int GAP_VERTICAL_PREVIEW = 12;

    // ========================================================
    // GRID LAYOUT GAPS
    // ========================================================

    /** Gap between columns in GridLayout for image columns. */
    public static final int GAP_GRID_COL_IMAGES = 30;

    /** Gap between columns in GridLayout for buttons. */
    public static final int GAP_GRID_COL_BUTTONS = 20;

    // ========================================================
    // BORDER WIDTHS
    // ========================================================

    /** Border width for image box outlines. */
    public static final int BORDER_WIDTH_IMAGE_BOX = 2;

    /** Border width for standard lines. */
    public static final int BORDER_WIDTH_STANDARD = 1;

    // ========================================================
    // EMPTY BORDER DIMENSIONS (top, left, bottom, right)
    // ========================================================

    /** Standard panel border. */
    public static final int BORDER_TOP_PANEL = 20;
    public static final int BORDER_LEFT_PANEL = 25;
    public static final int BORDER_BOTTOM_PANEL = 20;
    public static final int BORDER_RIGHT_PANEL = 25;

    /** Picker panel border. */
    public static final int BORDER_TOP_PICKER = 20;
    public static final int BORDER_LEFT_PICKER = 30;
    public static final int BORDER_BOTTOM_PICKER = 20;
    public static final int BORDER_RIGHT_PICKER = 30;

    /** Top controls border. */
    public static final int BORDER_TOP_TOP_CONTROLS = 15;
    public static final int BORDER_LEFT_TOP_CONTROLS = 20;
    public static final int BORDER_BOTTOM_TOP_CONTROLS = 15;
    public static final int BORDER_RIGHT_TOP_CONTROLS = 20;

    /** Title container border. */
    public static final int BORDER_TOP_TITLE = 10;
    public static final int BORDER_LEFT_TITLE = 15;
    public static final int BORDER_BOTTOM_TITLE = 10;
    public static final int BORDER_RIGHT_TITLE = 15;

    /** Image preview container border. */
    public static final int BORDER_TOP_PREVIEW = 15;
    public static final int BORDER_LEFT_PREVIEW = 15;
    public static final int BORDER_BOTTOM_PREVIEW = 15;
    public static final int BORDER_RIGHT_PREVIEW = 15;

    // ========================================================
    // VERTICAL STRUT HEIGHTS (spacing between components)
    // ========================================================
    /** Standard vertical strut height. */
    public static final int STRUT_STANDARD = 15;

    // ========================================================
    // CONSTRAINT VALUES
    // ========================================================

    /** Minimum image dimension. */
    public static final int MIN_IMAGE_DIMENSION = 80;

    /** Image preview padding to subtract from container. */
    public static final int IMAGE_PREVIEW_PADDING = 40;

    /** Image preview title height to subtract from container. */
    public static final int IMAGE_PREVIEW_TITLE_HEIGHT = 80;

}

