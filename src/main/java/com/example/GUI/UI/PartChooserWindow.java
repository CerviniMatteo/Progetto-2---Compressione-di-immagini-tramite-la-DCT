package com.example.GUI.UI;

import com.example.GUI.enums.ButtonStyle;
import com.example.GUI.enums.PanelContrast;
import com.example.assignment.Part1;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import java.awt.*;

import static com.example.GUI.factory.StylingFactory.getStyledButton;
import static com.example.GUI.factory.StylingFactory.getStyledPanel;

/**
 * Entry point window that allows users to select which assignment part to execute.
 * <p>
 * Presents two buttons:
 * <ul>
 *   <li><strong>Part 1:</strong> Runs DCT benchmarking across various block sizes</li>
 *   <li><strong>Part 2:</strong> Launches the interactive image compression GUI</li>
 * </ul>
 */
public class PartChooserWindow extends JFrame {

    /**
     * Logger used to track UI actions and warnings.
     */
    private static final Log log =
            LogFactory.getLog(PartChooserWindow.class);

    /**
     * Constructs and displays the part chooser window.
     * <p>
     * Creates two buttons:
     * <ul>
     *   <li><strong>Part 1 Button:</strong> Invokes {@link Part1#benchmark(int[])} with predefined block sizes</li>
     *   <li><strong>Part 2 Button:</strong> Instantiates {@link ImageCompressionWindow} for interactive compression</li>
     * </ul>
     */
    public PartChooserWindow() {
        super("Choose what assignment part to be run");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(250, 100);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JButton part1Button = getStyledButton("Part1", ButtonStyle.STYLE1);
        JButton part2Button = getStyledButton("Part2", ButtonStyle.STYLE1);

        part1Button.addActionListener(e -> {
            log.info("Part 1 selected");
            Part1 part1 = new Part1();
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() {
                    part1.benchmark(new int[]{8, 16, 32, 64, 128, 256, 512, 1048});
                    return null;
                }
            }.execute();
        });

        part2Button.addActionListener(e -> {
            log.info("Part 2 selected");
            new ImageCompressionWindow();
        });

        JPanel buttonPanel = getStyledPanel(PanelContrast.HIGH);
        buttonPanel.setLayout(new FlowLayout());

        buttonPanel.add(part1Button);
        buttonPanel.add(part2Button);

        add(buttonPanel, BorderLayout.CENTER);

        setVisible(true);
    }
}