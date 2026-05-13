package com.example.GUI.UI;

import com.example.GUI.enums.ButtonStyle;
import com.example.GUI.enums.PanelContrast;
import com.example.assignment.Part2;
import com.example.lib.utils.ImageUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.math3.util.Pair;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
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
     * Holds references to the preview boxes for updating after image operations.
     */
    private JPanel originalBox;
    private JPanel compressedBox;


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

        JPanel topButtonsPanel = createTopPanel();
        JPanel imagesPanel = createImagesPanel();

        add(topButtonsPanel, BorderLayout.NORTH);
        add(imagesPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    /**
     * Creates the top button panel with "Choose Image" and "Compress Image" controls.
     *
     * @return configured top panel
     */
    private JPanel createTopPanel() {
        JPanel topButtonsPanel = getStyledPanel(PanelContrast.HIGH);

        JButton chooseImageButton =
                getStyledButton("Choose Image", ButtonStyle.STYLE2);

        JButton compressButton =
                getStyledButton("Compress Image", ButtonStyle.STYLE3);

        topButtonsPanel.add(chooseImageButton);
        topButtonsPanel.add(Box.createHorizontalStrut(20));
        topButtonsPanel.add(compressButton);

        // Wire button actions
        chooseImageButton.addActionListener(e -> handleChooseImage());
        compressButton.addActionListener(e -> handleCompress());

        return topButtonsPanel;
    }

    /**
     * Creates the bottom panel with side-by-side image preview boxes.
     *
     * @return configured images panel
     */
    private JPanel createImagesPanel() {
        JPanel imagesPanel = getStyledPanel(PanelContrast.MEDIUM);
        imagesPanel.setLayout(new GridLayout(1, 2, 20, 0));

        originalBox = createImageBox("Original");
        compressedBox = createImageBox("Compressed");

        imagesPanel.add(originalBox);
        imagesPanel.add(compressedBox);

        return imagesPanel;
    }

    /**
     * Handles the "Choose Image" button action.
     * <p>
     * Creates an {@link ImagePicker} and subscribes to its image selection events.
     * When an image is selected, stores a deep copy and displays it in the original box.
     * </p>
     */
    private void handleChooseImage() {
        log.debug("Opening image picker dialog");
        ImagePicker imagePicker = new ImagePicker();

        imagePicker.subscribe(pair -> {
            selectedImageName = extractFilename(pair.getFirst());
            selectedImage = ImageUtils.copyBufferedImage(pair.getSecond());

            log.info(String.format("Image selected: %s (size: %dx%d pixels)",
                    selectedImageName, selectedImage.getWidth(), selectedImage.getHeight()));

            showImage(originalBox, selectedImage, selectedImageName);
        });

        imagePicker.showUI();
    }

    /**
     * Handles the "Compress Image" button action.
     * <p>
     * If an image is selected, creates an {@link IntegersPicker} and waits for compression
     * parameters. Once received, compresses a copy of the original and displays the result.
     * </p>
     */
    private void handleCompress() {
        if (selectedImage == null) {
            log.warn("Compress action triggered but no image selected");
            return;
        }

        log.debug("Opening compression parameters picker");
        IntegersPicker integerPicker = new IntegersPicker();

        integerPicker.subscribe(pair -> {
            int F = pair.getFirst();
            int d = pair.getSecond();

            log.info(String.format("Compression started with parameters: F=%d, d=%d", F, d));

            // Compress a fresh copy to avoid mutating selectedImage
            BufferedImage selectedCopy = ImageUtils.copyBufferedImage(selectedImage);

            try {
                BufferedImage compressed = new Part2().compress(
                        new Pair<>(selectedImageName + "_compressed", selectedCopy),
                        F,
                        d
                );

                log.info(String.format("Compression completed: %s (size: %dx%d pixels)",
                        selectedImageName + "_compressed", compressed.getWidth(), compressed.getHeight()));

                showImage(compressedBox, compressed, selectedImageName + "_compressed");
            } catch (Exception e) {
                log.error("Compression failed: " + e.getMessage(), e);
            }
        });

        integerPicker.showUI();
    }

    /**
     * Extracts the base filename without extension.
     *
     * @param filenameWithExtension full filename (e.g., "image.bmp")
     * @return filename without extension (e.g., "image")
     */
    private String extractFilename(String filenameWithExtension) {
        int dotIndex = filenameWithExtension.lastIndexOf('.');
        return dotIndex > 0 ? filenameWithExtension.substring(0, dotIndex) : filenameWithExtension;
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
     * Converts to RGB, scales to fit, reads file size, and displays in a styled container.
     * </p>
     *
     * @param box target panel where the preview is rendered
     * @param image source image to preview
     * @param name base image name used for label and output file lookup
     */
    private void showImage(JPanel box, BufferedImage image, String name) {
        BufferedImage rgb = ImageUtils.toRgbImage(image);

        String sizeText = formatImageMetadata(image, name);

        int boxW = Math.max(box.getWidth() - 40, 100);
        int boxH = Math.max(box.getHeight() - 80, 80);
        Image scaled = ImageUtils.scaleImageToFit(rgb, boxW, boxH);

        JPanel container = createImageLabel(name, scaled, sizeText);

        box.removeAll();
        box.add(container, BorderLayout.CENTER);
        box.revalidate();
        box.repaint();
    }

    /**
     * Formats image metadata (dimensions and file size) as HTML.
     *
     * @param image the image to inspect
     * @param name output filename (used to look up the saved BMP)
     * @return HTML-formatted metadata string
     */
    private String formatImageMetadata(BufferedImage image, String name) {
        File file = new File("output/" + name + ".bmp");
        double kb = ImageUtils.fileSizeInKb(file);

        return String.format(
                "<html>%d x %d pixel <br> %.2f kB</html>",
                image.getWidth(),
                image.getHeight(),
                kb
        );
    }

    /**
     * Creates a preview container with image name, scaled image, and size information.
     *
     * @param name display name shown at the top
     * @param scaled scaled image used for preview
     * @param sizeText HTML-formatted dimensions and file size text
     * @return panel containing all preview UI elements
     */
    private static JPanel createImageLabel(String name, Image scaled, String sizeText) {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(Color.WHITE);
        container.setBorder(new EmptyBorder(10, 10, 10, 10));

        container.add(createMetadataLabel(name, true), BorderLayout.NORTH);
        container.add(new JLabel(new ImageIcon(scaled), SwingConstants.CENTER), BorderLayout.CENTER);
        container.add(createMetadataLabel(sizeText, false), BorderLayout.SOUTH);

        return container;
    }

    /**
     * Creates a formatted metadata label.
     *
     * @param text label text
     * @param isBold whether to apply bold font
     * @return styled label
     */
    private static JLabel createMetadataLabel(String text, boolean isBold) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Arial", isBold ? Font.BOLD : Font.PLAIN, isBold ? 30 : 20));

        if (!isBold) {
            label.setForeground(new Color(100, 100, 100));
        }

        return label;
    }
}