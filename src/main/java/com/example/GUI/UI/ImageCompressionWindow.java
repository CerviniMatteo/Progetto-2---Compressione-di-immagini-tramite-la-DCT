package com.example.GUI.UI;

import com.example.GUI.enums.ButtonStyle;
import com.example.GUI.enums.PanelContrast;
import com.example.assignment.Part2;
import com.example.lib.constants.GuiConstants;
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

        super(GuiConstants.APP_TITLE);

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
                getStyledButton(GuiConstants.BUTTON_CHOOSE_IMAGE, ButtonStyle.STYLE2);

        JButton compressButton =
                getStyledButton(GuiConstants.BUTTON_COMPRESS_IMAGE, ButtonStyle.STYLE3);

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

        originalBox = createImageBox(GuiConstants.LABEL_ORIGINAL);
        compressedBox = createImageBox(GuiConstants.LABEL_COMPRESSED);

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
        log.debug(GuiConstants.LOG_OPENING_IMAGE_PICKER);
        ImagePicker imagePicker = new ImagePicker();

        imagePicker.subscribe(pair -> {
            selectedImageName = extractFilename(pair.getFirst());
            selectedImage = ImageUtils.copyBufferedImage(pair.getSecond());

            log.info(String.format(GuiConstants.LOG_IMAGE_SELECTED,
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
            log.warn(GuiConstants.LOG_COMPRESS_WITHOUT_IMAGE);
            return;
        }

        log.debug(GuiConstants.LOG_OPENING_PARAMETERS_PICKER);
        IntegersPicker integerPicker = new IntegersPicker();

        integerPicker.subscribe(pair -> {
            int F = pair.getFirst();
            int d = pair.getSecond();

            log.info(String.format(GuiConstants.LOG_COMPRESSION_START, F, d));

            // Compress a fresh copy to avoid mutating selectedImage
            BufferedImage selectedCopy = ImageUtils.copyBufferedImage(selectedImage);

            try {
                BufferedImage compressed = new Part2().compress(
                        new Pair<>(selectedImageName + GuiConstants.COMPRESSED_SUFFIX, selectedCopy),
                        F,
                        d
                );

                log.info(String.format(GuiConstants.LOG_COMPRESSION_DONE,
                        selectedImageName + GuiConstants.COMPRESSED_SUFFIX, compressed.getWidth(), compressed.getHeight()));

                showImage(compressedBox, compressed, selectedImageName + GuiConstants.COMPRESSED_SUFFIX);
            } catch (Exception e) {
                log.error(GuiConstants.LOG_COMPRESSION_FAILED_PREFIX + e.getMessage(), e);
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

        label.setFont(new Font(GuiConstants.FONT_ARIAL, Font.BOLD, 30));

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
        File file = new File(GuiConstants.OUTPUT_DIR_NAME + File.separator + name + GuiConstants.FILE_EXTENSION_BMP);
        double kb = ImageUtils.fileSizeInKb(file);

        return String.format(
                GuiConstants.IMAGE_METADATA_TEMPLATE,
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
        label.setFont(new Font(GuiConstants.FONT_ARIAL, isBold ? Font.BOLD : Font.PLAIN, isBold ? 30 : 20));

        if (!isBold) {
            label.setForeground(new Color(100, 100, 100));
        }

        return label;
    }
}