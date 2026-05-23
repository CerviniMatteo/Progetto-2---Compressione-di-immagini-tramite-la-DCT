package com.example.GUI.utils;

import com.example.GUI.constants.UIStyleConstants;

import javax.swing.*;
import java.awt.*;

/**
 * Utility class for creating consistently styled dialogs throughout the application.
 * <p>
 * This class centralizes dialog creation logic, ensuring all dialogs share:
 * <ul>
 *   <li>Dark theme colors and styling</li>
 *   <li>Consistent fonts and layouts</li>
 *   <li>Proper background and foreground colors</li>
 * </ul>
 * </p>
 */
public class DialogCreator {

    /**
     * Creates a dark-themed error dialog.
     * <p>
     * Configures the dialog with:
     * <ul>
     *   <li>Dark background for all components</li>
     *   <li>White text for readability</li>
     *   <li>ERROR_MESSAGE type for appropriate icon</li>
     * </ul>
     * </p>
     *
     * @param parent the parent frame for the dialog
     * @param title the dialog title
     * @param errorMessage the error message to display
     * @return configured JDialog ready to display
     */
    public static JDialog createErrorDialog(JFrame parent, String title, String errorMessage) {
        JOptionPane optionPane = new JOptionPane(
                errorMessage,
                JOptionPane.ERROR_MESSAGE,
                JOptionPane.DEFAULT_OPTION
        );

        JDialog dialog = optionPane.createDialog(parent, title);

        // Apply dark theme styling
        applyDarkTheme(dialog);

        return dialog;
    }


    /**
     * Applies dark theme styling to a dialog and all its components.
     * <p>
     * Recursively applies dark background colors and white text to:
     * <ul>
     *   <li>Dialog background</li>
     *   <li>Content pane</li>
     *   <li>All nested panels</li>
     *   <li>All text labels</li>
     * </ul>
     * </p>
     *
     * @param dialog the dialog to style
     */
    private static void applyDarkTheme(JDialog dialog) {
        dialog.setBackground(UIStyleConstants.COLOR_DARK);

        JPanel panel = (JPanel) dialog.getContentPane();
        panel.setBackground(UIStyleConstants.COLOR_DARK);

        // Recursively apply dark theme to all components
        applyDarkThemeToComponents(panel.getComponents());
    }

    /**
     * Recursively applies dark theme to an array of components.
     *
     * @param components the components to style
     */
    private static void applyDarkThemeToComponents(Component[] components) {
        for (Component comp : components) {
            if (comp instanceof JPanel) {
                comp.setBackground(UIStyleConstants.COLOR_DARK);
                applyDarkThemeToComponents(((JPanel) comp).getComponents());
            } else if (comp instanceof JLabel || comp instanceof JButton) {
                comp.setForeground(Color.WHITE);
                comp.setBackground(UIStyleConstants.COLOR_DARK);
            } else {
                comp.setBackground(UIStyleConstants.COLOR_DARK);
                comp.setForeground(Color.WHITE);
            }
        }
    }

}

