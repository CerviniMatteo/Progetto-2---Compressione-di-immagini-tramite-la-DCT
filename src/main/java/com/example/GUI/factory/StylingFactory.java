package com.example.GUI.factory;

import com.example.GUI.enums.Style;

import javax.swing.*;
import java.awt.*;

/**
 * Factory class responsible for creating consistently styled Swing components.
 * <p>
 * The styling options are defined by the {@link Style} enum and are applied
 * through helper methods such as {@link #getStyledButton(String, Style)} and
 * {@link #getStyledTextField(int)}.
 */
public class StylingFactory {

    /**
     * Creates a Swing {@link JButton} with shared base properties and a
     * background color selected by the provided {@link Style}.
     *
     * @param text  the label text displayed on the button
     * @param style the visual style variant to apply
     * @return a new styled {@link JButton}
     */
    public static JButton getStyledButton(String text, Style style) {
        JButton button = new JButton(text);

        // Base visual settings applied to all button styles.
        button.setFocusPainted(false);
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 13));
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        // Style-specific background color selection.
        switch (style) {
            case STYLE1:
                button.setBackground(new Color(70, 130, 180));
                break;
            case STYLE2:
                button.setBackground(new Color(120, 34, 139));
                break;
            case STYLE3:
                button.setBackground(new Color(220, 150, 20));
                break;
        }

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
     *
     * @param dimension the number of columns used to size the text field
     * @return a new styled {@link JTextField}
     */
    public static JTextField getStyledTextField(int dimension) {
        JTextField field = new JTextField(dimension);

        // Dark theme colors.
        field.setBackground(new Color(45, 45, 45));
        field.setForeground(Color.WHITE);

        // Cursor and text selection styling.
        field.setCaretColor(Color.WHITE);
        field.setSelectionColor(new Color(70, 130, 180));
        field.setSelectedTextColor(Color.WHITE);

        // Typography.
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));

        // Subtle outline + internal padding.
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 70, 70), 1),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));

        return field;
    }
}