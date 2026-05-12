package com.example.UI;

import com.example.assignment.Part2;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.math3.util.Pair;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Main Swing window for the DCT image compression demo.
 * <p>
 * This frame provides:
 * <ul>
 *   <li>A left control panel to choose an input image and trigger compression</li>
 *   <li>A right display area showing the original and compressed images</li>
 *   <li>Integration with {@link ImagePicker} and {@link IntegersPicker} to gather user input</li>
 * </ul>
 * Compression is delegated to {@link Part2#compress(Pair, int, int)}.
 */
public class ImageCompressionWindow extends JFrame {

    /**
     * Logger used to track UI actions and warnings.
     */
    private static final Log log =
            LogFactory.getLog(ImageCompressionWindow.class);

    /**
     * Currently selected image to be compressed.
     * <p>
     * Set when the user chooses an image through {@link ImagePicker}.
     */
    private BufferedImage selectedImage;

    /**
     * currently selected image name to be compressed.
     *  <p>
     * Set when the user chooses an image through {@link ImagePicker}.
     */
    private String selectedImageName;

    /**
     * Builds and wires the full UI:
     * <ul>
     *   <li>Creates left and right panels</li>
     *   <li>Initializes buttons and picker dialogs</li>
     *   <li>Subscribes to picker events</li>
     *   <li>Configures compression flow and image rendering</li>
     * </ul>
     */
    public ImageCompressionWindow() {
        super("DCT-based Image Compression");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // =========================
        // LEFT PANEL
        // =========================
        JPanel leftPanel = new JPanel();
        leftPanel.setPreferredSize(new Dimension(220, 1024));
        leftPanel.setBackground(Color.BLUE);
        leftPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 20));

        JButton chooseImageButton = new JButton("Choose image");
        JButton compressButton = new JButton("Compress image");

        leftPanel.add(chooseImageButton);
        leftPanel.add(compressButton);

        // =========================
        // RIGHT PANEL
        // =========================
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
        rightPanel.setBackground(Color.LIGHT_GRAY);

        JPanel originalBox = createImageBox("Original");
        JPanel compressedBox = createImageBox("Compressed");

        // =========================
        // PICKERS
        // =========================
        ImagePicker imagePicker = new ImagePicker();
        IntegersPicker integerPicker = new IntegersPicker();

        // =========================
        // IMAGE SELECTION
        // =========================
        imagePicker.subscribe(pair -> {

            selectedImageName = pair.getFirst();
            selectedImage = pair.getSecond();

            log.info("Selected image: " + pair.getFirst());

            showImage(originalBox, selectedImage);
        });

        chooseImageButton.addActionListener(e -> imagePicker.showUI());

        // =========================
        // COMPRESSION FLOW
        // =========================
        compressButton.addActionListener(e -> {

            if (selectedImage == null) {
                log.warn("No image selected!");
                return;
            }

            integerPicker.subscribe(pair -> {

                int F = pair.getFirst();
                int d = pair.getSecond();

                log.info("Compressing with F=" + F + " d=" + d);

                Part2 part2 = new Part2();

                BufferedImage compressed =
                        part2.compress(new Pair<>(selectedImageName,selectedImage), F, d);

                showImage(compressedBox, compressed);
            });

            integerPicker.showUI();
        });

        // =========================
        // FRAME ASSEMBLY
        // =========================
        rightPanel.add(originalBox);
        rightPanel.add(compressedBox);

        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    // =========================
    // UI HELPERS
    // =========================

    /**
     * Creates a bordered panel used as an image container.
     * <p>
     * The panel is initialized with a centered text label (typically "Original" or "Compressed")
     * that is later replaced by an image.
     *
     * @param title placeholder title shown before an image is rendered
     * @return a configured panel ready to host an image preview
     */
    private JPanel createImageBox(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new LineBorder(Color.BLACK, 2));
        panel.setPreferredSize(new Dimension(450, 300));

        panel.add(new JLabel(title, SwingConstants.CENTER),
                BorderLayout.CENTER);

        return panel;
    }

    /**
     * Renders a scaled preview of the given image inside the target box.
     * <p>
     * This method clears existing content, inserts a new {@link JLabel} with
     * the scaled {@link ImageIcon}, and refreshes layout/painting.
     *
     * @param box   panel where the image preview should be shown
     * @param image source image to scale and display
     */
    private void showImage(JPanel box, BufferedImage image) {

        BufferedImage rgb = new BufferedImage(
                image.getWidth(),
                image.getHeight(),
                BufferedImage.TYPE_INT_RGB
        );

        Graphics2D g = rgb.createGraphics();
        g.drawImage(image, 0, 0, Color.WHITE, null);
        g.dispose();

        Image scaled = rgb.getScaledInstance(
                430,
                280,
                Image.SCALE_SMOOTH
        );

        JLabel label = new JLabel(new ImageIcon(scaled));

        box.removeAll();
        box.add(label, BorderLayout.CENTER);
        box.revalidate();
        box.repaint();
    }
}