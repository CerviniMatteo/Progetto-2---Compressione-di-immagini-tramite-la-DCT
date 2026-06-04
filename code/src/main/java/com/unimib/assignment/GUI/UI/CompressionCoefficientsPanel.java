package com.unimib.assignment.GUI.UI;

import com.unimib.assignment.GUI.factory.StylingFactory;
import com.unimib.assignment.GUI.constants.GUIConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

import static com.unimib.assignment.GUI.factory.StylingFactory.getStyledHeadingLabel;
import static com.unimib.assignment.GUI.factory.StylingFactory.getStyledLabel;
import static com.unimib.assignment.GUI.constants.PickerConstants.*;
import static com.unimib.assignment.GUI.constants.UIStyleConstants.*;
import static com.unimib.assignment.GUI.enums.ButtonStyle.STYLE1;

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
         setBackground(COLOR_DARK);
         setBorder(new EmptyBorder(
                 StylingFactory.scale(BORDER_TOP_PICKER),
                 StylingFactory.scale(BORDER_LEFT_PICKER),
                 StylingFactory.scale(BORDER_BOTTOM_PICKER),
                 StylingFactory.scale(BORDER_RIGHT_PICKER)
         ));

         // Title
         JLabel titleLabel = getStyledHeadingLabel(GUIConstants.COMPRESSION_PARAMETERS_TITLE);
         titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
         titleLabel.setForeground(COLOR_STEELBLUE);
         add(titleLabel);
         add(Box.createVerticalStrut(StylingFactory.scale(STRUT_STANDARD)));

         // Input fields panel
         JPanel fieldsPanel = new JPanel();
         fieldsPanel.setLayout(new GridLayout(2, 2, StylingFactory.scale(15), StylingFactory.scale(12)));
         fieldsPanel.setBackground(COLOR_DARK);
         fieldsPanel.setBorder(new LineBorder(COLOR_BORDER_DARK, StylingFactory.scale(BORDER_WIDTH_THIN)));
         fieldsPanel.setBorder(new EmptyBorder(
                 StylingFactory.scale(12),
                 StylingFactory.scale(12),
                 StylingFactory.scale(12),
                 StylingFactory.scale(12)
         ));

         JLabel fLabel = getStyledLabel(F);
         fLabel.setFont(new Font(GUIConstants.FONT_ARIAL, Font.BOLD, StylingFactory.scale(FONT_SIZE_FORM_LABEL)));
         firstField = StylingFactory.getStyledTextField(textFieldColumns);
         firstField.setFont(new Font(GUIConstants.FONT_SANS_SERIF, Font.PLAIN, StylingFactory.scale(FONT_SIZE_FORM_LABEL)));

         JLabel dLabel = getStyledLabel(D);
         dLabel.setFont(new Font(GUIConstants.FONT_ARIAL, Font.BOLD, StylingFactory.scale(FONT_SIZE_FORM_LABEL)));
         secondField = StylingFactory.getStyledTextField(textFieldColumns);
         secondField.setFont(new Font(GUIConstants.FONT_SANS_SERIF, Font.PLAIN, StylingFactory.scale(FONT_SIZE_FORM_LABEL)));

         fieldsPanel.add(fLabel);
         fieldsPanel.add(firstField);
         fieldsPanel.add(dLabel);
         fieldsPanel.add(secondField);

         add(fieldsPanel);
         add(Box.createVerticalStrut(StylingFactory.scale(STRUT_STANDARD)));

         // Button panel
         JPanel buttonPanel = new JPanel();
         buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
         buttonPanel.setBackground(COLOR_DARK);

         submitButton = StylingFactory.getStyledButton(SUBMIT, STYLE1);
         submitButton.setFont(new Font(GUIConstants.FONT_ARIAL, Font.BOLD, StylingFactory.scale(FONT_SIZE_SMALL)));
         submitButton.setPreferredSize(new Dimension(
                 StylingFactory.scale(BUTTON_WIDTH_SUBMIT),
                 StylingFactory.scale(BUTTON_HEIGHT_SUBMIT)
         ));

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

