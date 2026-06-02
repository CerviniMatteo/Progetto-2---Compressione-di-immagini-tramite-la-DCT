package com.unimib.assignment;

import com.unimib.assignment.GUI.UI.PartChooserWindow;

/**
 * Main entry point for the DCT-based image processing application.
 * <p>
 * This application consists of two parts:
 * </p>
 * <ol>
 *   <li><strong>Part 1:</strong> Runs a DCT benchmark comparing the custom DCT implementation
 *       with the JTransforms library DCT across various matrix sizes. Results are plotted and
 *       exported to CSV.</li>
 *   <li><strong>Part 2:</strong> Provides a UI for image selection and applies DCT-based lossy
 *       compression with user-configurable parameters. The compressed image is displayed
 *       side-by-side with the original.</li>
 * </ol>
 */
public class Application {

    /**
     * Main method that runs the main UI window.
     *
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        new PartChooserWindow();
    }
}