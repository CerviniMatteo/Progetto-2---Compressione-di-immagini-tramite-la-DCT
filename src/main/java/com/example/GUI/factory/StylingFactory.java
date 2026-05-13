package com.example.GUI.factory;

import com.example.GUI.enums.ButtonStyle;
import com.example.GUI.enums.PanelContrast;
import com.example.lib.constants.GuiConstants;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * Factory class responsible for creating consistently styled Swing components.
 * <p>
 * This class centralizes all UI styling logic, ensuring consistent colors, fonts,
 * borders, and padding across the application. It uses internal constants to avoid
 * hardcoded values and provides helper methods for common styling patterns.
 * </p>
 *
 * @see ButtonStyle
 * @see PanelContrast
 */
public class StylingFactory {

    // ========================================================
    // COLOR PALETTE
    // ========================================================

    /** Steelblue primary color used in buttons and accents. */
    private static final Color COLOR_STEELBLUE = new Color(70, 130, 180);

    /** Purple accent color for secondary buttons. */
    private static final Color COLOR_PURPLE = new Color(120, 34, 139);

    /** Gold/orange accent color for tertiary buttons. */
    private static final Color COLOR_GOLD = new Color(220, 150, 20);

    /** Dark background for high-contrast panels. */
    private static final Color COLOR_DARK = new Color(30, 30, 30);

    /** Medium dark background for medium-contrast panels. */
    private static final Color COLOR_MEDIUM_DARK = new Color(45, 45, 45);

    /** Light gray background for low-contrast panels. */
    private static final Color COLOR_LIGHT_GRAY = new Color(170, 170, 170);

    /** Border color for light panels. */
    private static final Color COLOR_BORDER_LIGHT = new Color(200, 200, 200);

    /** Border color for text fields and dark elements. */
    private static final Color COLOR_BORDER_DARK = new Color(70, 70, 70);

    /** Border color for accents. */
    private static final Color COLOR_BORDER_ACCENT = new Color(70, 130, 180);

    /** White text and foreground. */
    private static final Color COLOR_TEXT_LIGHT = Color.WHITE;

    // ========================================================
    // FONT CONSTANTS
    // ========================================================

    /** Standard button font size. */
    private static final int FONT_SIZE_BUTTON = 20;

    /** Standard text field font size. */
    private static final int FONT_SIZE_TEXTFIELD = 20;

    /** Title font size. */
    private static final int FONT_SIZE_TITLE = 32;

    /** Heading font size. */
    private static final int FONT_SIZE_HEADING = 24;

    /** Subheading font size. */
    private static final int FONT_SIZE_SUBHEADING = 18;


    // ========================================================
    // PADDING CONSTANTS (top, left, bottom, right)
    // ========================================================

    /** Standard button padding. */
    private static final int PAD_BUTTON_V = 10;
    private static final int PAD_BUTTON_H = 15;

    /** Standard panel padding. */
    private static final int PAD_PANEL = 20;

    /** Standard text field padding. */
    private static final int PAD_TEXTFIELD_V = 5;
    private static final int PAD_TEXTFIELD_H = 8;

    /** Light box padding. */
    private static final int PAD_LIGHT_BOX = 10;

    // ========================================================
    // BORDER CONSTANTS
    // ========================================================

    /** Border width for light panel outlines. */
    private static final int BORDER_WIDTH_LIGHT = 1;

    /** Border width for text field outlines. */
    private static final int BORDER_WIDTH_THIN = 1;

    /**
     * Creates a Swing {@link JButton} with shared base properties and a
     * background color selected by the provided {@link ButtonStyle}.
     *
     * @param text the label text displayed on the button
     * @param buttonStyle the visual style variant to apply
     * @return a new styled {@link JButton}
     */
    public static JButton getStyledButton(String text, ButtonStyle buttonStyle) {
        JButton button = new JButton(text);

        // Apply base styling
        applyButtonBaseStyle(button);

        // Apply style-specific color
        button.setBackground(getButtonColorForStyle(buttonStyle));

        return button;
    }

    /**
     * Creates a styled {@link JTextField} configured for a dark theme UI.
     * <p>
     * The method applies:
     * <ul>
     *   <li>dark background and white foreground for contrast,</li>
     *   <li>custom caret and selection colors for readability,</li>
     *   <li>a consistent font,</li>
     *   <li>a compound border with outer line and inner padding.</li>
     * </ul>
     * </p>
     *
     * @param dimension the number of columns used to size the text field
     * @return a new styled {@link JTextField}
     */
    public static JTextField getStyledTextField(int dimension) {
        JTextField field = new JTextField(dimension);

        // Colors and contrast
        field.setBackground(COLOR_MEDIUM_DARK);
        field.setForeground(COLOR_TEXT_LIGHT);
        field.setCaretColor(COLOR_TEXT_LIGHT);
        field.setSelectionColor(COLOR_STEELBLUE);
        field.setSelectedTextColor(COLOR_TEXT_LIGHT);

        // Typography
        field.setFont(new Font(GuiConstants.FONT_SANS_SERIF, Font.PLAIN, FONT_SIZE_TEXTFIELD));

        // Border: subtle outline + internal padding
        field.setBorder(createTextFieldBorder());

        return field;
    }

    /**
     * Creates a styled {@link JPanel} with background and border based on contrast level.
     * <p>
     * {@link PanelContrast#HIGH} is for dark backgrounds with no padding.
     * {@link PanelContrast#MEDIUM} is for medium-dark backgrounds with standard padding.
     * {@link PanelContrast#LOW} is for light backgrounds with bordered box styling.
     * </p>
     *
     * @param contrast the contrast variant to apply
     * @return a new styled {@link JPanel}
     */
    public static JPanel getStyledPanel(PanelContrast contrast) {
        JPanel panel = new JPanel();

        switch (contrast) {
            case HIGH:
                panel.setBackground(COLOR_DARK);
                break;
            case MEDIUM:
                panel.setBackground(COLOR_MEDIUM_DARK);
                panel.setBorder(new EmptyBorder(PAD_PANEL, PAD_PANEL, PAD_PANEL, PAD_PANEL));
                break;
            case LOW:
                panel.setBackground(COLOR_LIGHT_GRAY);
                panel.setBorder(createLightBoxBorder());
                break;
        }

        return panel;
    }

    /**
     * Creates a styled {@link JLabel} for titles.
     *
     * @param text the label text
     * @return a new styled title label
     */
    public static JLabel getStyledTitleLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font(GuiConstants.FONT_ARIAL, Font.BOLD, FONT_SIZE_TITLE));
        label.setForeground(COLOR_TEXT_LIGHT);
        return label;
    }

    /**
     * Creates a styled {@link JLabel} for headings.
     *
     * @param text the label text
     * @return a new styled heading label
     */
    public static JLabel getStyledHeadingLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.LEFT);
        label.setFont(new Font(GuiConstants.FONT_ARIAL, Font.BOLD, FONT_SIZE_HEADING));
        label.setForeground(COLOR_TEXT_LIGHT);
        return label;
    }

    /**
     * Creates a styled {@link JLabel} for regular text.
     *
     * @param text the label text
     * @return a new styled label
     */
    public static JLabel getStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font(GuiConstants.FONT_ARIAL, Font.PLAIN, FONT_SIZE_SUBHEADING));
        label.setForeground(COLOR_TEXT_LIGHT);
        return label;
    }

    /**
     * Creates a styled {@link JLabel} with custom alignment.
     *
     * @param text the label text
     * @param horizontalAlignment the horizontal alignment (e.g., SwingConstants.CENTER)
     * @return a new styled label
     */
    public static JLabel getStyledLabel(String text, int horizontalAlignment) {
        JLabel label = new JLabel(text, horizontalAlignment);
        label.setFont(new Font(GuiConstants.FONT_ARIAL, Font.PLAIN, FONT_SIZE_SUBHEADING));
        label.setForeground(COLOR_TEXT_LIGHT);
        return label;
    }

    /**
     * Styles a {@link JFrame} for dark theme consistency.
     *
     * @param frame the frame to style
     */
    public static void styleFrame(JFrame frame) {
        frame.setBackground(COLOR_DARK);
        frame.getContentPane().setBackground(COLOR_DARK);
    }

    // ========================================================
    // PRIVATE HELPERS
    // ========================================================

    /**
     * Returns the background color for a given button style.
     *
     * @param style button style variant
     * @return color for the button background
     */
    private static Color getButtonColorForStyle(ButtonStyle style) {
        return switch (style) {
            case STYLE1 -> COLOR_STEELBLUE;
            case STYLE2 -> COLOR_PURPLE;
            case STYLE3 -> COLOR_GOLD;
            default -> COLOR_STEELBLUE;
        };
    }

    /**
     * Applies shared base styling to all buttons.
     *
     * @param button button to style
     */
    private static void applyButtonBaseStyle(JButton button) {
        button.setFocusPainted(false);
        button.setForeground(COLOR_TEXT_LIGHT);
        button.setFont(new Font(GuiConstants.FONT_ARIAL, Font.BOLD, FONT_SIZE_BUTTON));
        button.setBorder(BorderFactory.createEmptyBorder(PAD_BUTTON_V, PAD_BUTTON_H, PAD_BUTTON_V, PAD_BUTTON_H));
    }

    /**
     * Creates a text field border: line outline + empty padding.
     *
     * @return compound border suitable for text fields
     */
    private static Border createTextFieldBorder() {
        return BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_BORDER_DARK, BORDER_WIDTH_THIN),
                new EmptyBorder(PAD_TEXTFIELD_V, PAD_TEXTFIELD_H, PAD_TEXTFIELD_V, PAD_TEXTFIELD_H)
        );
    }

    /**
     * Creates a light box border: rounded line + empty padding.
     *
     * @return compound border suitable for light-contrast panels
     */
    private static Border createLightBoxBorder() {
        return BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_BORDER_LIGHT, BORDER_WIDTH_LIGHT, true),
                new EmptyBorder(PAD_LIGHT_BOX, PAD_LIGHT_BOX, PAD_LIGHT_BOX, PAD_LIGHT_BOX)
        );
    }
}