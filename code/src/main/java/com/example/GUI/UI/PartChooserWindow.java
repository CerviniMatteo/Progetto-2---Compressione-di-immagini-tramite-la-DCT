package com.example.GUI.UI;

import com.example.GUI.enums.ButtonStyle;
import com.example.GUI.enums.PanelContrast;
import com.example.GUI.constants.GUIConstants;
import com.example.launcher.Part1Launcher;
import com.formdev.flatlaf.FlatDarkLaf;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.CancellationException;
import java.util.concurrent.atomic.AtomicBoolean;
import static com.example.GUI.constants.GUIConstants.LOG_PART1_SELECTED;
import static com.example.GUI.constants.GUIConstants.LOG_PART2_SELECTED;
import static com.example.GUI.factory.StylingFactory.*;
import static com.example.assignment.constants.BenchmarkConstants.BENCHMARK_ERROR;

/**
 * Entry point window that allows users to select which assignment part to execute.
 * <p>Presents two buttons:</p>
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
    private static final int WINDOW_WIDTH = 700;

    /** Window height in pixels. */
    private static final int WINDOW_HEIGHT = 280;

    /** Button style for part selection buttons. */
    private static final ButtonStyle BUTTON_STYLE = ButtonStyle.STYLE1;

    /** Button style for stop button. */
    private static final ButtonStyle RED_BUTTON_STYLE = ButtonStyle.STYLE4;

    /** Button for launching Part 1 benchmark. */
    private JButton part1Button = null;

    /** Button for launching Part 2 GUI. */
    private JButton part2Button = null;

    /** Button for stopping benchmark. */
    private JButton endBenchmarkButton = null;

    /** Panel containing Part 1 buttons. */
    private JPanel leftPanel = null;

    /** Active benchmark worker. */
    private SwingWorker<Void, Void> benchmarkWorker = null;

    /** Cancellation flag for benchmark runs. */
    private final AtomicBoolean benchmarkCancelled = new AtomicBoolean(false);

    /** Logger used to track UI actions and warnings. */
    private static final Log log = LogFactory.getLog(PartChooserWindow.class);

    /**
     * Constructs and displays the part chooser window.
     */
    public PartChooserWindow() {

        super(GUIConstants.PART_CHOOSER_TITLE);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        styleFrame(this);

        JPanel mainPanel = createMainPanel();
        add(mainPanel, BorderLayout.CENTER);

        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception e) {
            log.error(GUIConstants.LOG_THEME_APPLY_FAILED, e);
        }

        setVisible(true);
    }

    /**
     * Creates the main panel with title, description, and buttons.
     *
     * @return configured main panel
     */
    private JPanel createMainPanel() {

        JPanel mainPanel = getStyledPanel(PanelContrast.HIGH);
        mainPanel.setLayout(new BorderLayout(0, 20));
        mainPanel.setBorder(new EmptyBorder(30, 40, 30, 40));
        mainPanel.add(createTitleSection(), BorderLayout.NORTH);
        mainPanel.add(createButtonPanel(), BorderLayout.CENTER);

        return mainPanel;
    }

    /**
     * Creates the title and description section.
     *
     * @return configured title panel
     */
    private JPanel createTitleSection() {

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(30, 30, 30));

        JLabel titleLabel = getStyledTitleLabel(GUIConstants.DCT_IMAGE_COMPRESSION_TITLE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel descriptionLabel = getStyledLabel(GUIConstants.PART_CHOOSER_DESCRIPTION, SwingConstants.CENTER);
        descriptionLabel.setFont(new Font(GUIConstants.FONT_ARIAL, Font.PLAIN, 16));
        descriptionLabel.setForeground(new Color(150, 150, 150));
        descriptionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(descriptionLabel);

        return panel;
    }

    /**
     * Creates the button panel with Part1 and Part2 buttons.
     *
     * @return configured button panel
     */
    private JPanel createButtonPanel() {

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(1, 2, 20, 0));
        panel.setBackground(new Color(30, 30, 30));

        part1Button        = getStyledButton(GUIConstants.PART1_BUTTON_HTML,          BUTTON_STYLE);
        part2Button        = getStyledButton(GUIConstants.PART2_BUTTON_HTML,          BUTTON_STYLE);
        endBenchmarkButton = getStyledButton(GUIConstants.STOP_BENCHMARK_BUTTON_HTML, RED_BUTTON_STYLE);

        Dimension buttonSize = new Dimension(250, 60);
        part1Button.setPreferredSize(buttonSize);
        part2Button.setPreferredSize(buttonSize);
        endBenchmarkButton.setPreferredSize(buttonSize);

        endBenchmarkButton.addActionListener(e -> {
            log.info(GUIConstants.LOG_BENCHMARK_THREAD_CANCELED);
            benchmarkCancelled.set(true);
            if (benchmarkWorker != null && !benchmarkWorker.isDone()) {
                benchmarkWorker.cancel(true);
            }
        });

        part1Button.addActionListener(e -> {
            try {
                handlePart1();
            } catch (CancellationException ex) {
                log.error(BENCHMARK_ERROR, ex);
            }
        });

        part2Button.addActionListener(e -> handlePart2());

        leftPanel = new JPanel();
        leftPanel.setLayout(new BorderLayout(0, 10));
        leftPanel.setBackground(new Color(30, 30, 30));
        leftPanel.add(part1Button, BorderLayout.CENTER);

        panel.add(leftPanel);
        panel.add(part2Button);

        return panel;
    }

    /**
     * Handles the Part 1 button action.
     */
    private void handlePart1() {

        log.info(LOG_PART1_SELECTED);
        enableButtons(false);

        leftPanel.add(endBenchmarkButton, BorderLayout.SOUTH);
        leftPanel.revalidate();
        leftPanel.repaint();

        benchmarkWorker = new SwingWorker<>() {

            @Override
            protected Void doInBackground() throws Exception {
                Part1Launcher part1Launcher = new  Part1Launcher(benchmarkCancelled);
                return part1Launcher.launchAndHandlePart1();
            }

            @Override
            protected void done() {
                try {
                    get();
                } catch (CancellationException ignored) {
                     // Cancellation is expected if the user stops the benchmark, so we ignore it here
                } catch (Exception e) {
                    log.error(BENCHMARK_ERROR, e);
                }
                leftPanel.remove(endBenchmarkButton);
                leftPanel.revalidate();
                leftPanel.repaint();

                enableButtons(true);
            }
        };

        benchmarkWorker.execute();
    }

    /**
     * Handles the Part 2 button action.
     */
    private void handlePart2() {

        enableButtons(false);
        log.info(LOG_PART2_SELECTED);
        ImageCompressionWindow window = new ImageCompressionWindow();
        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                PartChooserWindow.this.setEnabled(true);
                PartChooserWindow.this.toFront();
                enableButtons(true);
            }
        });
    }

    // ==================== UTILITIES ====================

    /**
     * Enables or disables the main buttons.
     *
     * @param enable true to enable, false to disable
     */
    public void enableButtons(boolean enable) {
        part1Button.setEnabled(enable);
        part2Button.setEnabled(enable);
    }
}