package com.example.GUI.UI;

import com.example.GUI.enums.ButtonStyle;
import com.example.GUI.enums.PanelContrast;
import com.example.assignment.Part1;
import com.example.lib.constants.GuiConstants;
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

    // ========================================================
    // CONSTANTS
    // ========================================================

    /** Window width in pixels. */
    private static final int WINDOW_WIDTH = 250;

    /** Window height in pixels. */
    private static final int WINDOW_HEIGHT = 100;

    /** Block sizes to benchmark (powers of 2). */
    private static final int[] BENCHMARK_BLOCK_SIZES = {8, 16, 32, 64, 128, 256, 512, 1024, 2048};

    /** Button style for part selection buttons. */
    private static final ButtonStyle BUTTON_STYLE = ButtonStyle.STYLE1;

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
        super(GuiConstants.PART_CHOOSER_TITLE);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    /**
     * Creates the button panel with Part1 and Part2 buttons.
     *
     * @return configured button panel
     */
    private JPanel createButtonPanel() {
        JPanel panel = getStyledPanel(PanelContrast.HIGH);
        panel.setLayout(new FlowLayout());

        JButton part1Button = getStyledButton(GuiConstants.BUTTON_PART1, BUTTON_STYLE);
        JButton part2Button = getStyledButton(GuiConstants.BUTTON_PART2, BUTTON_STYLE);

        part1Button.addActionListener(e -> handlePart1());
        part2Button.addActionListener(e -> handlePart2());

        panel.add(part1Button);
        panel.add(part2Button);

        return panel;
    }

    /**
     * Handles the Part 1 button action.
     * <p>
     * Runs the DCT benchmark in a background worker thread to avoid blocking the UI.
     * </p>
     */
    private void handlePart1() {
        log.info(GuiConstants.LOG_PART1_SELECTED);
        Part1 part1 = new Part1();

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                log.debug(GuiConstants.LOG_BENCHMARK_THREAD_START);
                part1.benchmark(BENCHMARK_BLOCK_SIZES);
                log.debug(GuiConstants.LOG_BENCHMARK_THREAD_DONE);
                return null;
            }
        }.execute();
    }

    /**
     * Handles the Part 2 button action.
     * <p>
     * Launches the interactive image compression window.
     * </p>
     */
    private void handlePart2() {
        log.info(GuiConstants.LOG_PART2_SELECTED);
        new ImageCompressionWindow();
    }
}