package com.example.GUI.UI;

import com.example.GUI.factory.StylingFactory;
import com.example.lib.constants.PickerConstants;
import com.example.GUI.observer.Observable;
import org.apache.commons.math3.util.Pair;

import javax.swing.*;
import java.awt.*;

import static com.example.GUI.enums.Style.STYLE1;
import static com.example.lib.constants.PickerConstants.*;

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
 * @see com.example.lib.constants.PickerConstants
 */
public class IntegersPicker extends JFrame {

    /**
     * Text field where the user enters the first integer {@code F}.
     * Constructed with a visible column count of 10.
     */
    private final JTextField firstField;

    /**
     * Text field where the user enters the second integer {@code d}.
     * Constructed with a visible column count of 10.
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
     * The window title and UI text values come from {@link com.example.lib.constants.PickerConstants}.
     * The frame is sized to 300x150 and uses a {@link FlowLayout}. A submit button triggers
     * validation and publishing of the entered values.
     * </p>
     *
     * @see com.example.lib.constants.PickerConstants#COMPRESSION_FACTOR_PICKER
     * @see FlowLayout
     */
    public IntegersPicker() {

        super(COMPRESSION_FACTOR_PICKER);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(300, 150);
        setLayout(new FlowLayout());

        getContentPane().setBackground(new Color(30, 30, 30));

        JLabel fLabel = new JLabel(F);
        fLabel.setForeground(Color.WHITE);

        JLabel dLabel = new JLabel(D);
        dLabel.setForeground(Color.WHITE);

        add(fLabel);
        firstField = StylingFactory.getStyledTextField(8);
        add(firstField);

        add(dLabel);
        secondField = StylingFactory.getStyledTextField(8);
        add(secondField);

        JButton submitButton =
                StylingFactory.getStyledButton(SUBMIT, STYLE1);

        add(submitButton);

        submitButton.addActionListener(e -> submit());
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
            int F = Integer.parseInt(firstField.getText().trim());
            int d = Integer.parseInt(secondField.getText().trim());

            if (F < 0) {
                throw new IllegalArgumentException(
                        F_POSITIVE_ERROR
                );
            }

            if (d < 0 || d > (2 * F) - 2) {
                throw new IllegalArgumentException(
                        D_VALUE_ERROR
                );
            }

            Pair<Integer, Integer> pair = new Pair<>(F, d);

            observable.set(pair);

            dispose();

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    PickerConstants.ERROR,
                    JOptionPane.ERROR_MESSAGE
            );
        }
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
    }
}