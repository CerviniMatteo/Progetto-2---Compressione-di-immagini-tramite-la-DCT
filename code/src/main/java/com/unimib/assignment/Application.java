package com.unimib.assignment;

import com.formdev.flatlaf.FlatDarkLaf;
import com.unimib.assignment.GUI.UI.PartChooserWindow;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Application entry point for the DCT-based image processing project.
 *
 * <p>This launcher initializes the Swing Look and Feel and opens the first UI
 * window used to choose which project part to run.</p>
 *
 * <h2>Project Parts</h2>
 * <ol>
 *   <li><b>Part 1</b>: Runs benchmark comparisons between a custom DCT implementation
 *       and JTransforms across multiple matrix sizes, then exports results in a CSV file.</li>
 *   <li><b>Part 2</b>: Opens a GUI workflow for image selection and DCT-based lossy
 *       compression with user-defined parameters, showing original and compressed images.</li>
 * </ol>
 */
public class Application {

    /**
     * Launches the desktop application.
     * @param args command-line arguments (currently unused)
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception ignored) {
            // If the custom Look and Feel cannot be loaded, continue with the default one.
        }

        // Ensure Swing components are created on the EDT to avoid threading issues.
        SwingUtilities.invokeLater(PartChooserWindow::new);
    }
}