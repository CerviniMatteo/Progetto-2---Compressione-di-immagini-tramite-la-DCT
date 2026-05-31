package com.unimib.assignment.GUI.constants;

import java.awt.*;

/**
 * Centralized constants for UI styling including colors, dimensions, fonts, and layout parameters.
 * <p>
 * This class is the single source of truth for every magic number used across the GUI.
 * It covers colors, font sizes, padding, border widths, gaps, window/button dimensions,
 * and spacing struts — for both the main application panels and the factory-created components.
 * </p>
 *
 * <p>All numeric values are defined as <em>baselines</em> for a 96 DPI / 1080p screen.
 * {@link com.unimib.assignment.GUI.factory.StylingFactory} applies a runtime scale factor
 * so every value adapts automatically to different screen densities and resolutions.</p>
 */
public final class UIStyleConstants {

    private UIStyleConstants() {}

    // ========================================================
    // COLORS — BACKGROUNDS
    // ========================================================

    /** Dark background color used throughout the UI (high-contrast panels, frames). */
    public static final Color COLOR_DARK = new Color(30, 30, 30);

    /** Medium dark background color for panels and text fields. */
    public static final Color COLOR_MEDIUM_DARK = new Color(45, 45, 45);

    /** Light gray background for low-contrast (light box) panels. */
    public static final Color COLOR_LIGHT_GRAY = new Color(170, 170, 170);

    // ========================================================
    // COLORS — ACCENTS & BUTTONS
    // ========================================================

    /** Steel blue — primary button color and text-field selection highlight. */
    public static final Color COLOR_STEELBLUE = new Color(70, 130, 180);

    /** Purple — secondary button color (STYLE2). */
    public static final Color COLOR_PURPLE = new Color(120, 34, 139);

    /** Gold/orange — tertiary button color (STYLE3). */
    public static final Color COLOR_GOLD = new Color(220, 150, 20);

    /** Red — destructive/warning button color (STYLE4). */
    public static final Color COLOR_RED = new Color(188, 4, 4);

    // ========================================================
    // COLORS — TEXT & BORDERS
    // ========================================================

    /** White — primary foreground color for text and icons on dark backgrounds. */
    public static final Color COLOR_TEXT_LIGHT = Color.WHITE;

    /** Gray color for placeholder or disabled text. */
    public static final Color COLOR_GRAY_PLACEHOLDER = new Color(150, 150, 150);

    /** Border color for text fields and dark-themed outlines. */
    public static final Color COLOR_BORDER_DARK = new Color(70, 70, 70);

    /** Border color for light-panel and image-box outlines. */
    public static final Color COLOR_BORDER_LIGHT = new Color(200, 200, 200);

    /** Gray color for generic borders and dividers. */
    public static final Color COLOR_GRAY_BORDER = new Color(100, 100, 100);

    // ========================================================
    // FONT SIZES (baseline: 96 DPI / 1080p)
    // ========================================================

    /** Font size for main titles (e.g. window headings). */
    public static final int FONT_SIZE_TITLE = 32;

    /** Font size for section headings. */
    public static final int FONT_SIZE_HEADING = 24;

    /** Font size for sub-headings and body labels. */
    public static final int FONT_SIZE_SUBHEADING = 18;

    /** Font size for buttons. */
    public static final int FONT_SIZE_BUTTON = 16;

    /** Font size for text fields. */
    public static final int FONT_SIZE_TEXTFIELD = 20;
    /** Font size for form labels. */
    public static final int FONT_SIZE_FORM_LABEL = 16;

    /** Font size for smaller labels and descriptions. */
    public static final int FONT_SIZE_SMALL = 14;

    // ========================================================
    // PADDING (baseline: 96 DPI / 1080p)
    // ========================================================

    /** Vertical padding inside buttons. */
    public static final int PAD_BUTTON_V = 10;

    /** Horizontal padding inside buttons. */
    public static final int PAD_BUTTON_H = 15;

    /** Uniform padding inside medium-contrast panels. */
    public static final int PAD_PANEL = 20;

    /** Vertical padding inside text fields. */
    public static final int PAD_TEXTFIELD_V = 5;

    /** Horizontal padding inside text fields. */
    public static final int PAD_TEXTFIELD_H = 8;

    /** Padding inside low-contrast (light box) panels. */
    public static final int PAD_LIGHT_BOX = 10;

    // ========================================================
    // EMPTY BORDER DIMENSIONS — PANELS (top, left, bottom, right)
    // ========================================================

    /** Standard panel border. */
    public static final int BORDER_TOP_PANEL    = 20;
    public static final int BORDER_LEFT_PANEL   = 25;
    public static final int BORDER_BOTTOM_PANEL = 20;
    public static final int BORDER_RIGHT_PANEL  = 25;

    /** Picker panel border. */
    public static final int BORDER_TOP_PICKER    = 20;
    public static final int BORDER_LEFT_PICKER   = 30;
    public static final int BORDER_BOTTOM_PICKER = 20;
    public static final int BORDER_RIGHT_PICKER  = 30;

    /** Top controls panel border. */
    public static final int BORDER_TOP_TOP_CONTROLS    = 15;
    public static final int BORDER_LEFT_TOP_CONTROLS   = 20;
    public static final int BORDER_BOTTOM_TOP_CONTROLS = 15;
    public static final int BORDER_RIGHT_TOP_CONTROLS  = 20;
    // ========================================================
    // BORDER WIDTHS (baseline: 96 DPI / 1080p)
    // ========================================================

    /** Border width for standard / generic lines. */
    public static final int BORDER_WIDTH_STANDARD = 1;

    /** Border width for light panel outlines. */
    public static final int BORDER_WIDTH_LIGHT = 1;

    /** Border width for text field outlines. */
    public static final int BORDER_WIDTH_THIN = 1;

    // ========================================================
    // LAYOUT GAPS (baseline: 96 DPI / 1080p)
    // ========================================================

    /** Standard horizontal gap (BorderLayout, flow panels). */
    public static final int GAP_HORIZONTAL_STANDARD = 15;

    /** Standard vertical gap (BorderLayout, flow panels). */
    public static final int GAP_VERTICAL_STANDARD = 10;
    /** Gap between columns in GridLayout for buttons. */
    public static final int GAP_GRID_COL_BUTTONS = 20;

    // ========================================================
    // VERTICAL STRUT HEIGHTS (spacing between components)
    // ========================================================

    /** Standard vertical strut height. */
    public static final int STRUT_STANDARD = 15;

    // ========================================================
    // WINDOW & DIALOG DIMENSIONS (baseline: 96 DPI / 1080p)
    // ========================================================

    /** Compression Coefficients Picker window width. */
    public static final int WINDOW_WIDTH_PICKER = 500;

    /** Compression Coefficients Picker window height. */
    public static final int WINDOW_HEIGHT_PICKER = 300;

    /** File Chooser dialog width. */
    public static final int DIALOG_WIDTH_FILE_CHOOSER = 512;

    /** File Chooser dialog height. */
    public static final int DIALOG_HEIGHT_FILE_CHOOSER = 400;

    // ========================================================
    // BUTTON DIMENSIONS (baseline: 96 DPI / 1080p)
    // ========================================================

    /** Standard button width in Part Chooser. */
    public static final int BUTTON_WIDTH_PART_CHOOSER = 250;

    /** Standard button height in Part Chooser. */
    public static final int BUTTON_HEIGHT_PART_CHOOSER = 60;

    /** Submit button width in pickers. */
    public static final int BUTTON_WIDTH_SUBMIT = 120;

    /** Submit button height in pickers. */
    public static final int BUTTON_HEIGHT_SUBMIT = 40;
}