package com.example.GUI.UI;

import com.example.GUI.factory.StylingFactory;
import com.example.GUI.constants.GUIConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

import static com.example.GUI.factory.StylingFactory.getStyledHeadingLabel;
import static com.example.GUI.factory.StylingFactory.getStyledLabel;
import static com.example.GUI.constants.PickerConstants.*;
import static com.example.GUI.enums.ButtonStyle.STYLE1;

/**
 * Panel that builds the UI for the compression coefficients picker.
 * This class encapsulates creation of all Swing components (labels, text fields,
 * and the submit button) so the containing frame can remain lightweight and only
 * handle events and logic.
 */
public class CompressionCoefficientsPanel extends JPanel {

    private final JTextField firstField;
    private final JTextField secondField;
    private final JButton submitButton;

    public CompressionCoefficientsPanel(int textFieldColumns) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(30, 30, 30));
        setBorder(new EmptyBorder(20, 30, 20, 30));

        // Title
        JLabel titleLabel = getStyledHeadingLabel(GUIConstants.COMPRESSION_PARAMETERS_TITLE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setForeground(new Color(70, 130, 180));
        add(titleLabel);
        add(Box.createVerticalStrut(15));

        // Input fields panel
        JPanel fieldsPanel = new JPanel();
        fieldsPanel.setLayout(new GridLayout(2, 2, 15, 12));
        fieldsPanel.setBackground(new Color(30, 30, 30));
        fieldsPanel.setBorder(new LineBorder(new Color(70, 70, 70), 1));
        fieldsPanel.setBorder(new EmptyBorder(12, 12, 12, 12));

        JLabel fLabel = getStyledLabel(F);
        fLabel.setFont(new Font(GUIConstants.FONT_ARIAL, Font.BOLD, 16));
        firstField = StylingFactory.getStyledTextField(textFieldColumns);
        firstField.setFont(new Font(GUIConstants.FONT_SANS_SERIF, Font.PLAIN, 16));

        JLabel dLabel = getStyledLabel(D);
        dLabel.setFont(new Font(GUIConstants.FONT_ARIAL, Font.BOLD, 16));
        secondField = StylingFactory.getStyledTextField(textFieldColumns);
        secondField.setFont(new Font(GUIConstants.FONT_SANS_SERIF, Font.PLAIN, 16));

        fieldsPanel.add(fLabel);
        fieldsPanel.add(firstField);
        fieldsPanel.add(dLabel);
        fieldsPanel.add(secondField);

        add(fieldsPanel);
        add(Box.createVerticalStrut(15));

        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        buttonPanel.setBackground(new Color(30, 30, 30));

        submitButton = StylingFactory.getStyledButton(SUBMIT, STYLE1);
        submitButton.setFont(new Font(GUIConstants.FONT_ARIAL, Font.BOLD, 14));
        submitButton.setPreferredSize(new Dimension(120, 40));

        buttonPanel.add(submitButton);

        add(buttonPanel);
    }

    public JTextField getFirstField() {
        return firstField;
    }

    public JTextField getSecondField() {
        return secondField;
    }

    public JButton getSubmitButton() {
        return submitButton;
    }
}

