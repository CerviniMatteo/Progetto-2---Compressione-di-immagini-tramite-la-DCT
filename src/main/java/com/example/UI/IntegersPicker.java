package com.example.UI;

import com.example.lib.constants.PickerConstants;
import com.example.lib.utils.Observable;
import org.apache.commons.math3.util.Pair;

import javax.swing.*;
import java.awt.*;

import static com.example.lib.constants.PickerConstants.*;

public class IntegersPicker extends JFrame {

    private final JTextField firstField = new JTextField(10);
    private final JTextField secondField = new JTextField(10);

    private final Observable<Pair<Integer, Integer>> observable =
            new Observable<>();

    public IntegersPicker() {

        super(COMPRESSION_FACTOR_PICKER);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(300, 150);
        setLayout(new FlowLayout());

        add(new JLabel(F));
        add(firstField);

        add(new JLabel(D));
        add(secondField);

        JButton submitButton = new JButton(SUBMIT);
        add(submitButton);

        submitButton.addActionListener(e -> submit());
    }

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

    public void subscribe(java.util.function.Consumer<Pair<Integer, Integer>> c) {
        observable.subscribe(c);
    }

    public void showUI() {
        setLocationRelativeTo(null);
        setVisible(true);
    }
}