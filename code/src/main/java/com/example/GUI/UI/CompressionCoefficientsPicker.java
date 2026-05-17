package com.example.GUI.UI;

import com.example.GUI.observer.Observable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.math3.util.Pair;

import javax.swing.*;
import java.awt.*;
import static com.example.GUI.factory.StylingFactory.*;
import static com.example.GUI.constants.PickerConstants.*;

/**
 * GUI component for collecting two integer parameters used by the application.
 * <p>
 * This {@link javax.swing.JFrame}-based picker prompts the user to enter two integer
 * values: {@code F} (a compression factor) and {@code d} (a dependent integer constrained
 * by {@code F}). Entered values are validated and, when valid, published via an
 * {@link Observable} as a {@link org.apache.commons.math3.util.Pair}
 * containing {@code (F, d)}.
 * </p>
 *
 * <p>Validation rules:</p>
 * <ul>
 *   <li>{@code F} must be non-negative (F >= 0)</li>
 *   <li>{@code d} d must satisfy: 0 $le d $le 2*F - 2</li>
 * </ul>
 *
 * @see Observable
 * @see org.apache.commons.math3.util.Pair
 */
public class CompressionCoefficientsPicker extends JFrame {

    // ========================================================
    // CONSTANTS
    // ========================================================

    /** Window width in pixels. */
    private static final int WINDOW_WIDTH = 500;

    /** Window height in pixels. */
    private static final int WINDOW_HEIGHT = 300;

    /** Text field column count for integer input. */
    private static final int TEXT_FIELD_COLUMNS = 8;


    /**
     * Logger for parameter picker events and validation errors.
     */
    private static final Log log = LogFactory.getLog(CompressionCoefficientsPicker.class);

    /**
     * Text field where the user enters the first integer {@code F}.
     */
    private final JTextField firstField;

    /**
     * Text field where the user enters the second integer {@code d}.
     */
    private final JTextField secondField;

    /**
     * Observable used to notify subscribers when the user submits valid integers.
     * The observable publishes a {@link org.apache.commons.math3.util.Pair} where
     * {@code getFirst()} returns {@code F} and {@code getSecond()} returns {@code d}.
     */
    private final Observable<Pair<Integer, Integer>> observable =
            new Observable<>();

    /**
     * Create a new integers picker window.
     * <p>
     * The frame is sized to 300x150 and uses a {@link FlowLayout}. A submit button triggers
     * validation and publishing of the entered values.
     * </p>
     *
     * @see FlowLayout
     */
    public CompressionCoefficientsPicker(int rows, int cols) {

        super(COMPRESSION_FACTOR_PICKER);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setLayout(new BorderLayout(10, 10));
        // Apply dark theme styling
        styleFrame(this);
        getContentPane().setBackground(new Color(30, 30, 30));

        // Build UI in a separate panel class and reuse here
        CompressionCoefficientsPanel uiPanel = new CompressionCoefficientsPanel(TEXT_FIELD_COLUMNS);

        // assign fields from the panel
        firstField = uiPanel.getFirstField();
        secondField = uiPanel.getSecondField();

        add(uiPanel, BorderLayout.CENTER);

        // attach submit action
        JButton submitButton = uiPanel.getSubmitButton();
        submitButton.addActionListener(e -> submit(rows, cols));

        log.debug(LOG_INTEGER_PICKER_INITIALIZED);
    }


    /**
     * Read, validate and publish the integers entered in the UI.
     * <p>This method:</p>
     * <ol>
     *   <li>Parses the contents of the text fields into integers</li>
     *   <li>Validates them according to the rules: {@code F}{@code F >= 0} and {@code 0 $le d && d $le 2*F - 2}</li>
     *   <li>If validation succeeds, creates a {@link org.apache.commons.math3.util.Pair} of {@code (F, d)},
     *       sets it on the {@link Observable} and disposes the window</li>
     *   <li>If parsing or validation fails, shows an error dialog to the user with an explanatory message</li>
     * </ol>
     *
     * @throws IllegalArgumentException if parsing fails (e.g. non-numeric input) or if validation rules are not met.
     *                                  Note: this exception is caught within the method and presented to the user
     *                                  as an error dialog (see {@link javax.swing.JOptionPane}).
     */
    private void submit(int cols, int rows) {
        try {
            String fText = firstField.getText().trim();
            String dText = secondField.getText().trim();

            log.debug(String.format(LOG_PARSE_INPUTS, fText, dText));

            int F = Integer.parseInt(fText);
            int d = Integer.parseInt(dText);

            log.debug(String.format(LOG_PARSED_INPUTS, F, d));

            validateInputs(F, d, rows, cols);

            log.info(String.format(LOG_VALIDATION_SUCCESS, F, d));

            observable.set(new Pair<>(F, d));
            dispose();

        } catch (NumberFormatException e) {
            String errorMsg = INVALID_INTEGER_INPUT_ERROR;
            log.warn(errorMsg);
            showErrorDialog(errorMsg);
        } catch (IllegalArgumentException ex) {
            log.warn(LOG_VALIDATION_FAILED_PREFIX + ex.getMessage());
            showErrorDialog(ex.getMessage());
        }
    }

    /**
     * Validates the input parameters against business rules.
     *
     * @param F compression factor (must be >= 0)
     * @param d dependent parameter (must be 0 $le d $le 2*F - 2)
     * @throws IllegalArgumentException if validation fails
     */
    private void validateInputs(int F, int d, int rows, int cols) {
        if (F < 0) {
            throw new IllegalArgumentException(F_POSITIVE_ERROR);
        }

        if (d < 0 || d > (2 * F) - 2) {
            throw new IllegalArgumentException(D_VALUE_ERROR);
        }

        if(rows < F || cols < F){
            throw new IllegalArgumentException(F_ROWS_COLS_ERROR);
        }
    }

    /**
     * Displays an error dialog to the user with dark theme styling.
     *
     * @param message error message to display
     */
    private void showErrorDialog(String message) {
        JOptionPane optionPane = new JOptionPane(
                message,
                JOptionPane.ERROR_MESSAGE,
                JOptionPane.DEFAULT_OPTION
        );

        JDialog dialog = optionPane.createDialog(this, com.example.GUI.constants.PickerConstants.ERROR);
        dialog.setBackground(new Color(30, 30, 30));
        
        JPanel panel = (JPanel) dialog.getContentPane();
        panel.setBackground(new Color(30, 30, 30));
        
        for (Component comp : panel.getComponents()) {
            if (comp instanceof JPanel) {
                comp.setBackground(new Color(30, 30, 30));
            }
            comp.setBackground(new Color(30, 30, 30));
            comp.setForeground(Color.WHITE);
        }
        
        dialog.setVisible(true);
    }

    /**
     * Subscribe a consumer that will be notified when the user submits valid integers.
     * <p>
     * The consumer receives a {@link org.apache.commons.math3.util.Pair} containing
     * {@code (F, d)}; call {@code getFirst()} to obtain {@code F} and {@code getSecond()}
     * to obtain {@code d}.
     * </p>
     *
     * @param c a {@code java.util.function.Consumer} that accepts a {@link org.apache.commons.math3.util.Pair}
     *          containing the submitted integers {@code (F, d)}.
     */
    public void subscribe(java.util.function.Consumer<Pair<Integer, Integer>> c) {
        observable.subscribe(c);
    }

    /**
     * Center the window on screen and make it visible.
     * <p>
     * Call this after constructing the picker and subscribing to receive the selected values.
     * </p>
     */
    public void showUI() {
        setLocationRelativeTo(null);
        setVisible(true);
        log.debug(LOG_INTEGER_PICKER_SHOWN);
    }
}