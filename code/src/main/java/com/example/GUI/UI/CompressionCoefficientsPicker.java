package com.example.GUI.UI;

import com.example.GUI.factory.StylingFactory;
import com.example.GUI.constants.PickerConstants;
import com.example.GUI.constants.GUIConstants;
import com.example.GUI.observer.Observable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.math3.util.Pair;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

import static com.example.GUI.enums.ButtonStyle.STYLE1;
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
 * <p>
 * Validation rules:
 * <ul>
 *   <li>{@code F} must be non-negative (F >= 0)</li>
 *   <li>{@code d} must satisfy 0 <= d <= 2*F - 2</li>
 * </ul>
 * </p>
 *
 * @see Observable
 * @see org.apache.commons.math3.util.Pair
 * @see PickerConstants
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
     * The window title and UI text values come from {@link PickerConstants}.
     * The frame is sized to 300x150 and uses a {@link FlowLayout}. A submit button triggers
     * validation and publishing of the entered values.
     * </p>
     *
     * @see PickerConstants#COMPRESSION_FACTOR_PICKER
     * @see FlowLayout
     */
    public CompressionCoefficientsPicker() {

        super(COMPRESSION_FACTOR_PICKER);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setLayout(new BorderLayout(10, 10));

        // Apply dark theme styling
        styleFrame(this);
        getContentPane().setBackground(new Color(30, 30, 30));

        // Main panel with padding and title
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(30, 30, 30));
        mainPanel.setBorder(new EmptyBorder(20, 30, 20, 30));

        // Title
        JLabel titleLabel = getStyledHeadingLabel(GUIConstants.COMPRESSION_PARAMETERS_TITLE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setForeground(new Color(70, 130, 180));
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(15));

        // Input fields panel
        JPanel fieldsPanel = new JPanel();
        fieldsPanel.setLayout(new GridLayout(2, 2, 15, 12));
        fieldsPanel.setBackground(new Color(30, 30, 30));
        fieldsPanel.setBorder(new LineBorder(new Color(70, 70, 70), 1));
        fieldsPanel.setBorder(new EmptyBorder(12, 12, 12, 12));

        JLabel fLabel = getStyledLabel(F);
        fLabel.setFont(new Font(GUIConstants.FONT_ARIAL, Font.BOLD, 16));
        firstField = StylingFactory.getStyledTextField(TEXT_FIELD_COLUMNS);
        firstField.setFont(new Font(GUIConstants.FONT_SANS_SERIF, Font.PLAIN, 16));

        JLabel dLabel = getStyledLabel(D);
        dLabel.setFont(new Font(GUIConstants.FONT_ARIAL, Font.BOLD, 16));
        secondField = StylingFactory.getStyledTextField(TEXT_FIELD_COLUMNS);
        secondField.setFont(new Font(GUIConstants.FONT_SANS_SERIF, Font.PLAIN, 16));

        fieldsPanel.add(fLabel);
        fieldsPanel.add(firstField);
        fieldsPanel.add(dLabel);
        fieldsPanel.add(secondField);

        mainPanel.add(fieldsPanel);
        mainPanel.add(Box.createVerticalStrut(15));

        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        buttonPanel.setBackground(new Color(30, 30, 30));

        JButton submitButton = StylingFactory.getStyledButton(SUBMIT, STYLE1);
        submitButton.setFont(new Font(GUIConstants.FONT_ARIAL, Font.BOLD, 14));
        submitButton.setPreferredSize(new Dimension(120, 40));

        buttonPanel.add(submitButton);

        mainPanel.add(buttonPanel);

        add(mainPanel, BorderLayout.CENTER);

        submitButton.addActionListener(e -> submit());
        log.debug(LOG_INTEGER_PICKER_INITIALIZED);
    }


    /**
     * Read, validate and publish the integers entered in the UI.
     * <p>
     * This method:
     * <ol>
     *   <li>Parses the contents of the text fields into integers</li>
     *   <li>Validates them according to the rules: {@code F} >= 0 and {@code 0 <= d <= 2*F-2}</li>
     *   <li>If validation succeeds, creates a {@link org.apache.commons.math3.util.Pair} of {@code (F, d)},
     *       sets it on the {@link Observable} and disposes the window</li>
     *   <li>If parsing or validation fails, shows an error dialog to the user with an explanatory message</li>
     * </ol>
     * </p>
     *
     * @throws IllegalArgumentException if parsing fails (e.g. non-numeric input) or if validation rules are not met.
     *                                  Note: this exception is caught within the method and presented to the user
     *                                  as an error dialog (see {@link javax.swing.JOptionPane}).
     */
    private void submit() {
        try {
            String fText = firstField.getText().trim();
            String dText = secondField.getText().trim();

            log.debug(String.format(LOG_PARSE_INPUTS, fText, dText));

            int F = Integer.parseInt(fText);
            int d = Integer.parseInt(dText);

            log.debug(String.format(LOG_PARSED_INPUTS, F, d));

            validateInputs(F, d);

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
     * @param d dependent parameter (must be 0 <= d <= 2*F - 2)
     * @throws IllegalArgumentException if validation fails
     */
    private void validateInputs(int F, int d) {
        if (F < 0) {
            throw new IllegalArgumentException(F_POSITIVE_ERROR);
        }

        if (d < 0 || d > (2 * F) - 2) {
            throw new IllegalArgumentException(D_VALUE_ERROR);
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

        JDialog dialog = optionPane.createDialog(this, PickerConstants.ERROR);
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