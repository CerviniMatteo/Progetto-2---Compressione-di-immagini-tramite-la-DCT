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
     * Creates a dark-themed information dialog.
     * <p>
     * Configures the dialog with:
     * <ul>
     *   <li>Dark background for all components</li>
     *   <li>White text for readability</li>
     *   <li>INFORMATION_MESSAGE type for appropriate icon</li>
     * </ul>
     * </p>
     *
     * @param parent the parent frame for the dialog
     * @param title the dialog title
     * @param message the message to display
     * @return configured JDialog ready to display
     */
    @SuppressWarnings("unused")
    public static JDialog createInfoDialog(JFrame parent, String title, String message) {
        JOptionPane optionPane = new JOptionPane(
                message,
                JOptionPane.INFORMATION_MESSAGE,
                JOptionPane.DEFAULT_OPTION
        );

        JDialog dialog = optionPane.createDialog(parent, title);

        // Apply dark theme styling
        applyDarkTheme(dialog);

        return dialog;
    }

    /**
     * Creates a dark-themed warning dialog.
     * <p>
     * Configures the dialog with:
     * <ul>
     *   <li>Dark background for all components</li>
     *   <li>White text for readability</li>
     *   <li>WARNING_MESSAGE type for appropriate icon</li>
     * </ul>
     * </p>
     *
     * @param parent the parent frame for the dialog
     * @param title the dialog title
     * @param warningMessage the warning message to display
     * @return configured JDialog ready to display
     */
    @SuppressWarnings("unused")
    public static JDialog createWarningDialog(JFrame parent, String title, String warningMessage) {
        JOptionPane optionPane = new JOptionPane(
                warningMessage,
                JOptionPane.WARNING_MESSAGE,
                JOptionPane.DEFAULT_OPTION
        );

        JDialog dialog = optionPane.createDialog(parent, title);

        // Apply dark theme styling
        applyDarkTheme(dialog);

        return dialog;
    }

    /**
     * Creates a dark-themed confirmation dialog (Yes/No).
     * <p>
     * Configures the dialog with:
     * <ul>
     *   <li>Dark background for all components</li>
     *   <li>White text for readability</li>
     *   <li>Yes/No buttons for confirmation</li>
     * </ul>
     * </p>
     *
     * @param parent the parent frame for the dialog
     * @param title the dialog title
     * @param message the confirmation message to display
     * @return JOptionPane.YES_OPTION if yes was selected, JOptionPane.NO_OPTION otherwise
     */
    @SuppressWarnings("unused")
    public static int createConfirmDialog(JFrame parent, String title, String message) {
        JOptionPane optionPane = new JOptionPane(
                message,
                JOptionPane.QUESTION_MESSAGE,
                JOptionPane.YES_NO_OPTION
        );

        JDialog dialog = optionPane.createDialog(parent, title);

        // Apply dark theme styling
        applyDarkTheme(dialog);

        dialog.setVisible(true);

        Object selectedValue = optionPane.getValue();
        if (selectedValue == null) return JOptionPane.CLOSED_OPTION;
        return (Integer) selectedValue;
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

