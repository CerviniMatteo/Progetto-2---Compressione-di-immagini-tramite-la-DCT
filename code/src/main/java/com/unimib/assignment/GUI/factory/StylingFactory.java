package com.unimib.assignment.GUI.factory;

import com.unimib.assignment.GUI.enums.ButtonStyle;
import com.unimib.assignment.GUI.enums.PanelContrast;
import com.unimib.assignment.GUI.constants.GUIConstants;
import com.unimib.assignment.GUI.constants.UIStyleConstants;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * Factory class responsible for creating consistently styled Swing components.
 * <p>
 * This class contains only logic — all visual constants (colors, font sizes,
 * paddings, border widths) are defined in {@link UIStyleConstants}. Pixel values
 * are scaled at runtime based on screen DPI and resolution so the UI adapts
 * correctly to different screen sizes and densities (e.g. HiDPI / Retina displays).
 * </p>
 *
 * <p>The baseline is a 96 DPI screen at 1080p. On other screens a combined scale
 * factor is computed once in the static initializer and applied to every size via
 * {@link #scale(int)}.</p>
 *
 * @see UIStyleConstants
 * @see ButtonStyle
 * @see PanelContrast
 */
public class StylingFactory {

    // ========================================================
    // SCALE FACTOR (computed once at class load)
    // ========================================================

    /**
     * Combined scale factor derived from screen DPI and vertical resolution.
     * <ul>
     *   <li>DPI factor  : actual DPI / 96   (standard desktop baseline)</li>
     *   <li>Res factor  : screen height / 1080 (Full HD baseline)</li>
     * </ul>
     * The two factors are averaged so neither alone dominates.
     */
    private static final float SCALE;

    static {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        float dpiScale = toolkit.getScreenResolution() / 96f;
        float resScale = toolkit.getScreenSize().height / 1080f;
        SCALE = (dpiScale + resScale) / 2f;
    }

    /**
     * Scales a baseline pixel value by the computed screen scale factor.
     *
     * @param value the baseline value defined for a 96 DPI / 1080p screen
     * @return the scaled value rounded to the nearest integer
     */
    public static int scale(int value) {
        return Math.round(value * SCALE);
    }

    // ========================================================
    // PREVENT INSTANTIATION
    // ========================================================

    private StylingFactory() {}

    // ========================================================
    // PUBLIC FACTORY METHODS
    // ========================================================

    /**
     * Creates a {@link JButton} with shared base properties and a background
     * color chosen by the provided {@link ButtonStyle}.
     *
     * @param text        the label text displayed on the button
     * @param buttonStyle the visual style variant to apply
     * @return a new styled {@link JButton}
     */
    public static JButton getStyledButton(String text, ButtonStyle buttonStyle) {
        JButton button = new JButton(text);
        applyButtonBaseStyle(button);
        button.setBackground(getButtonColorForStyle(buttonStyle));
        return button;
    }

    /**
     * Creates a styled {@link JTextField} configured for a dark-theme UI.
     * <p>Applies dark background/white foreground, custom caret and selection
     * colors, a scaled font, and a compound border with outer line and inner
     * padding.</p>
     *
     * @param dimension the number of columns used to size the text field
     * @return a new styled {@link JTextField}
     */
    public static JTextField getStyledTextField(int dimension) {
        JTextField field = new JTextField(dimension);

        field.setBackground(UIStyleConstants.COLOR_MEDIUM_DARK);
        field.setForeground(UIStyleConstants.COLOR_TEXT_LIGHT);
        field.setCaretColor(UIStyleConstants.COLOR_TEXT_LIGHT);
        field.setSelectionColor(UIStyleConstants.COLOR_STEELBLUE);
        field.setSelectedTextColor(UIStyleConstants.COLOR_TEXT_LIGHT);

        field.setFont(new Font(GUIConstants.FONT_SANS_SERIF, Font.PLAIN,
                scale(UIStyleConstants.FONT_SIZE_TEXTFIELD)));
        field.setBorder(createTextFieldBorder());

        return field;
    }

    /**
     * Creates a styled {@link JPanel} with background and border chosen by
     * contrast level.
     * <ul>
     *   <li>{@link PanelContrast#HIGH}   – dark background, no padding.</li>
     *   <li>{@link PanelContrast#MEDIUM} – medium-dark background, scaled padding.</li>
     *   <li>{@link PanelContrast#LOW}    – light background with bordered box styling.</li>
     * </ul>
     *
     * @param contrast the contrast variant to apply
     * @return a new styled {@link JPanel}
     * @throws IllegalArgumentException if an unhandled contrast value is passed
     */
    public static JPanel getStyledPanel(PanelContrast contrast) {
        JPanel panel = new JPanel();

        switch (contrast) {
            case HIGH:
                panel.setBackground(UIStyleConstants.COLOR_DARK);
                break;
            case MEDIUM:
                panel.setBackground(UIStyleConstants.COLOR_MEDIUM_DARK);
                int pad = scale(UIStyleConstants.PAD_PANEL);
                panel.setBorder(new EmptyBorder(pad, pad, pad, pad));
                break;
            case LOW:
                panel.setBackground(UIStyleConstants.COLOR_LIGHT_GRAY);
                panel.setBorder(createLightBoxBorder());
                break;
            default:
                throw new IllegalArgumentException("Unhandled PanelContrast: " + contrast);
        }

        return panel;
    }

    /**
     * Creates a styled {@link JLabel} for titles (centered, bold, large).
     *
     * @param text the label text
     * @return a new styled title label
     */
    public static JLabel getStyledTitleLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font(GUIConstants.FONT_ARIAL, Font.BOLD,
                scale(UIStyleConstants.FONT_SIZE_TITLE)));
        label.setForeground(UIStyleConstants.COLOR_TEXT_LIGHT);
        return label;
    }

    /**
     * Creates a styled {@link JLabel} for section headings (left-aligned, bold).
     *
     * @param text the label text
     * @return a new styled heading label
     */
    public static JLabel getStyledHeadingLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.LEFT);
        label.setFont(new Font(GUIConstants.FONT_ARIAL, Font.BOLD,
                scale(UIStyleConstants.FONT_SIZE_HEADING)));
        label.setForeground(UIStyleConstants.COLOR_TEXT_LIGHT);
        return label;
    }

    /**
     * Creates a styled {@link JLabel} for regular body text (left-aligned, plain).
     *
     * @param text the label text
     * @return a new styled label
     */
    public static JLabel getStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font(GUIConstants.FONT_ARIAL, Font.PLAIN,
                scale(UIStyleConstants.FONT_SIZE_SUBHEADING)));
        label.setForeground(UIStyleConstants.COLOR_TEXT_LIGHT);
        return label;
    }

    /**
     * Creates a styled {@link JLabel} with a custom horizontal alignment.
     *
     * @param text                the label text
     * @param horizontalAlignment the horizontal alignment constant
     *                            (e.g. {@link SwingConstants#CENTER})
     * @return a new styled label
     */
    @SuppressWarnings("MagicConstant")
    public static JLabel getStyledLabel(String text, int horizontalAlignment) {
        JLabel label = new JLabel(text, horizontalAlignment);
        label.setFont(new Font(GUIConstants.FONT_ARIAL, Font.PLAIN,
                scale(UIStyleConstants.FONT_SIZE_SUBHEADING)));
        label.setForeground(UIStyleConstants.COLOR_TEXT_LIGHT);
        return label;
    }

    /**
     * Applies dark-theme styling to a {@link JFrame}.
     *
     * @param frame the frame to style
     */
    public static void styleFrame(JFrame frame) {
        frame.setBackground(UIStyleConstants.COLOR_DARK);
        frame.getContentPane().setBackground(UIStyleConstants.COLOR_DARK);
    }

    // ========================================================
    // PRIVATE HELPERS
    // ========================================================

    /**
     * Returns the background {@link Color} for a given {@link ButtonStyle}.
     *
     * @param style the button style variant
     * @return the corresponding background color
     */
    private static Color getButtonColorForStyle(ButtonStyle style) {
        return switch (style) {
            case STYLE1 -> UIStyleConstants.COLOR_STEELBLUE;
            case STYLE2 -> UIStyleConstants.COLOR_PURPLE;
            case STYLE3 -> UIStyleConstants.COLOR_GOLD;
            case STYLE4 -> UIStyleConstants.COLOR_RED;
        };
    }

    /**
     * Applies shared base styling to all buttons.
     * <p>All sizes pass through {@link #scale(int)} so the button adapts to
     * screen density.</p>
     *
     * @param button the button to style
     */
    private static void applyButtonBaseStyle(JButton button) {
        button.setFocusPainted(false);
        button.setForeground(UIStyleConstants.COLOR_TEXT_LIGHT);
        button.setFont(new Font(GUIConstants.FONT_ARIAL, Font.BOLD,
                scale(UIStyleConstants.FONT_SIZE_BUTTON)));
        button.setBorder(BorderFactory.createEmptyBorder(
                scale(UIStyleConstants.PAD_BUTTON_V),
                scale(UIStyleConstants.PAD_BUTTON_H),
                scale(UIStyleConstants.PAD_BUTTON_V),
                scale(UIStyleConstants.PAD_BUTTON_H)
        ));
    }

    /**
     * Creates a text field border: a thin dark outline with scaled inner padding.
     *
     * @return compound border suitable for text fields
     */
    private static Border createTextFieldBorder() {
        return BorderFactory.createCompoundBorder(
                new LineBorder(UIStyleConstants.COLOR_BORDER_DARK,
                        scale(UIStyleConstants.BORDER_WIDTH_THIN)),
                new EmptyBorder(
                        scale(UIStyleConstants.PAD_TEXTFIELD_V),
                        scale(UIStyleConstants.PAD_TEXTFIELD_H),
                        scale(UIStyleConstants.PAD_TEXTFIELD_V),
                        scale(UIStyleConstants.PAD_TEXTFIELD_H)
                )
        );
    }

    /**
     * Creates a light box border: a rounded light outline with scaled inner padding.
     *
     * @return compound border suitable for light-contrast panels
     */
    private static Border createLightBoxBorder() {
        return BorderFactory.createCompoundBorder(
                new LineBorder(UIStyleConstants.COLOR_BORDER_LIGHT,
                        scale(UIStyleConstants.BORDER_WIDTH_LIGHT), true),
                new EmptyBorder(
                        scale(UIStyleConstants.PAD_LIGHT_BOX),
                        scale(UIStyleConstants.PAD_LIGHT_BOX),
                        scale(UIStyleConstants.PAD_LIGHT_BOX),
                        scale(UIStyleConstants.PAD_LIGHT_BOX)
                )
        );
    }
}