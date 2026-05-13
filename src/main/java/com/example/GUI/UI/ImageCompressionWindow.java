package com.example.GUI.UI;

import com.example.GUI.enums.ButtonStyle;
import com.example.GUI.enums.PanelContrast;
import com.example.assignment.Part2;
import com.example.lib.utils.ImageUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.math3.util.Pair;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

import static com.example.GUI.factory.StylingFactory.getStyledButton;
import static com.example.GUI.factory.StylingFactory.getStyledPanel;

/**
 * Main Swing window for the DCT image compression workflow.
 * <p>
 * This frame provides:
 * <ul>
 *   <li>Controls to pick an image and trigger compression</li>
 *   <li>Side-by-side previews for original and compressed images</li>
 *   <li>Integration with {@link ImagePicker} and {@link IntegersPicker}</li>
 * </ul>
 * </p>
 * <p>
 * Compression is delegated to {@link Part2#compress(Pair, int, int)}.
 * The original selected image is preserved by compressing a deep copy.
 * </p>
 */
public class ImageCompressionWindow extends JFrame {

    /**
     * Logger for UI events and warnings.
     */
    private static final Log log =
            LogFactory.getLog(ImageCompressionWindow.class);

    /**
     * Currently selected source image (kept as an unmodified copy).
     */
    private BufferedImage selectedImage;

    /**
     * Base name of the selected image file (without extension).
     */
    private String selectedImageName;

    /**
     * Builds and displays the image compression window.
     * <p>
     * The constructor:
     * <ol>
     *   <li>Creates top controls ("Choose Image" and "Compress Image")</li>
     *   <li>Creates side-by-side preview boxes ("Original" and "Compressed")</li>
     *   <li>Subscribes to image/parameter pickers</li>
     *   <li>Wires compression action and UI updates</li>
     * </ol>
     * </p>
     */
    public ImageCompressionWindow() {

        super("DCT Image Compression Tool");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ==================================================
        // TOP PANEL (BUTTONS)
        // ==================================================
        JPanel topButtonsPanel = getStyledPanel(PanelContrast.HIGH);

        JButton chooseImageButton =
                getStyledButton("Choose Image", ButtonStyle.STYLE2);

        JButton compressButton =
                getStyledButton("Compress Image", ButtonStyle.STYLE3);

        topButtonsPanel.add(chooseImageButton);
        topButtonsPanel.add(Box.createHorizontalStrut(20));
        topButtonsPanel.add(compressButton);

        // ==================================================
        // BOTTOM PANEL (IMAGES SIDE BY SIDE)
        // ==================================================
        JPanel imagesPanel = getStyledPanel(PanelContrast.MEDIUM);

        // GridLayout splits available width evenly across the two preview boxes
        imagesPanel.setLayout(new GridLayout(1, 2, 20, 0));

        JPanel originalBox = createImageBox("Original");

        JPanel compressedBox = createImageBox("Compressed");

        imagesPanel.add(originalBox);

        imagesPanel.add(compressedBox);

        // ==================================================
        // PICKERS
        // ==================================================
        ImagePicker imagePicker = new ImagePicker();

        IntegersPicker integerPicker = new IntegersPicker();

        imagePicker.subscribe(pair -> {

            selectedImageName =
                    pair.getFirst().substring(
                            0,
                            pair.getFirst().lastIndexOf('.')
                    );

            // Keep an internal deep copy so the original selected image remains unchanged.
            selectedImage =
                    ImageUtils.copyBufferedImage(pair.getSecond());

            log.info("Selected image: " + selectedImageName);

            showImage(
                    originalBox,
                    selectedImage,
                    selectedImageName
            );
        });

        chooseImageButton.addActionListener(
                e -> imagePicker.showUI()
        );

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

                // Compress a fresh copy to avoid mutating selectedImage.
                BufferedImage selectedCopy =
                        ImageUtils.copyBufferedImage(selectedImage);

                BufferedImage compressed = part2.compress(
                        new Pair<>(
                                selectedImageName + "_compressed",
                                selectedCopy
                        ),
                        F,
                        d
                );

                showImage(
                        compressedBox,
                        compressed,
                        selectedImageName + "_compressed"
                );
            });

            integerPicker.showUI();
        });

        // ==================================================
        // FRAME
        // ==================================================
        add(topButtonsPanel, BorderLayout.NORTH);

        add(imagesPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    // ==================================================
    // IMAGE BOX
    // ==================================================

    /**
     * Creates a stylized panel used as an image preview box.
     *
     * @param title title displayed when no image preview is currently shown
     * @return configured preview panel
     */
    private JPanel createImageBox(String title) {

        JPanel panel = getStyledPanel(PanelContrast.LOW);
        panel.setLayout(new BorderLayout());

        JLabel label = new JLabel(title, SwingConstants.CENTER);

        label.setFont(new Font("Arial", Font.BOLD, 30));

        label.setForeground(new Color(80, 80, 80));

        panel.add(label, BorderLayout.CENTER);

        return panel;
    }

    // ==================================================
    // IMAGE DISPLAY
    // ==================================================

    /**
     * Renders an image preview with metadata inside a target box.
     * <p>
     * This method:
     * <ul>
     *   <li>Converts the image to RGB for consistent rendering</li>
     *   <li>Reads output file size from {@code output/&lt;name&gt;.bmp}</li>
     *   <li>Scales image to fit the current box size while preserving aspect ratio</li>
     *   <li>Replaces existing box content with an updated preview container</li>
     * </ul>
     * </p>
     *
     * @param box target panel where the preview is rendered
     * @param image source image to preview
     * @param name base image name used for label and output file lookup
     */
    private void showImage(
            JPanel box,
            BufferedImage image,
            String name
    ) {

        BufferedImage rgb = new BufferedImage(
                image.getWidth(),
                image.getHeight(),
                BufferedImage.TYPE_INT_RGB
        );

        Graphics2D g = rgb.createGraphics();

        g.setColor(Color.WHITE);

        g.fillRect(
                0,
                0,
                rgb.getWidth(),
                rgb.getHeight()
        );

        g.drawImage(image, 0, 0, null);

        g.dispose();

        // ==================================================
        // FILE SIZE
        // ==================================================
        File file = new File("output/" + name + ".bmp");

        long bytes = file.length();

        double kb = bytes / 1024.0;

        String sizeText = String.format(
                "<html>%d x %d pixel <br> %.2f kB</html>",
                image.getWidth(),
                image.getHeight(),
                kb
        );

        // ==================================================
        // SCALE DYNAMICALLY BASED ON BOX SIZE
        // ==================================================

        // Internal padding + labels area compensation.
        int boxW = Math.max(box.getWidth() - 40, 100);
        int boxH = Math.max(box.getHeight() - 80, 80);

        // Preserve original image aspect ratio.
        double ratio = Math.min(
                (double) boxW / image.getWidth(),
                (double) boxH / image.getHeight()
        );

        int scaledW = (int) (image.getWidth() * ratio);
        int scaledH = (int) (image.getHeight() * ratio);

        Image scaled = rgb.getScaledInstance(
                scaledW,
                scaledH,
                Image.SCALE_SMOOTH
        );

        JPanel container = createImageLabel(name, scaled, sizeText);

        box.removeAll();

        box.add(container, BorderLayout.CENTER);

        box.revalidate();

        box.repaint();
    }

    /**
     * Creates a preview container with image name, scaled image, and size information.
     *
     * @param name display name shown at the top
     * @param scaled scaled image used for preview
     * @param sizeText HTML-formatted dimensions and file size text
     * @return panel containing all preview UI elements
     */
    private static JPanel createImageLabel(
            String name,
            Image scaled,
            String sizeText
    ) {

        JLabel imageLabel =
                new JLabel(new ImageIcon(scaled));

        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel nameLabel =
                new JLabel(name, SwingConstants.CENTER);

        nameLabel.setFont(new Font("Arial", Font.BOLD, 30));

        JLabel sizeLabel =
                new JLabel(sizeText, SwingConstants.CENTER);

        sizeLabel.setFont(new Font("Arial", Font.PLAIN, 20));

        sizeLabel.setForeground(new Color(100, 100, 100));

        JPanel container = new JPanel(new BorderLayout());

        container.setBackground(Color.WHITE);

        container.add(nameLabel, BorderLayout.NORTH);

        container.add(imageLabel, BorderLayout.CENTER);

        container.add(sizeLabel, BorderLayout.SOUTH);

        return container;
    }
}