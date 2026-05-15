package com.example.GUI.UI;

import com.example.GUI.enums.ButtonStyle;
import com.example.GUI.enums.PanelContrast;
import com.example.assignment.Part2;
import com.example.GUI.constants.GUIConstants;
import com.example.lib.utils.ImageUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.math3.util.Pair;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

import static com.example.GUI.factory.StylingFactory.*;

/**
 * Main Swing window for the DCT image compression workflow.
 * <p>This frame provides:</p>
 * <ul>
 *   <li>Controls to pick an image and trigger compression</li>
 *   <li>Side-by-side previews for original and compressed images</li>
 *   <li>Integration with {@link ImagePicker} and {@link CompressionCoefficientsPicker}</li>
 * </ul>
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
     * <p>The constructor:</p>
     * <ol>
     *   <li>Creates top controls ("Choose Image" and "Compress Image")</li>
     *   <li>Creates side-by-side preview boxes ("Original" and "Compressed")</li>
     *   <li>Subscribes to image/parameter pickers</li>
     *   <li>Wires compression action and UI updates</li>
     * </ol>
     */
    public ImageCompressionWindow() {

        super(GUIConstants.APP_TITLE);

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
        topButtonsPanel.setLayout(new BorderLayout(15, 10));
        topButtonsPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        // Title label
        JLabel titleLabel = getStyledHeadingLabel(GUIConstants.DCT_IMAGE_COMPRESSION_TITLE);

        // Buttons panel
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setBackground(new Color(30, 30, 30));
        buttonsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 0));

        JButton chooseImageButton =
                getStyledButton(GUIConstants.BUTTON_CHOOSE_IMAGE, ButtonStyle.STYLE2);

        JButton compressButton =
                getStyledButton(GUIConstants.BUTTON_COMPRESS_IMAGE, ButtonStyle.STYLE3);

        buttonsPanel.add(chooseImageButton);
        buttonsPanel.add(compressButton);

        topButtonsPanel.add(titleLabel, BorderLayout.WEST);
        topButtonsPanel.add(buttonsPanel, BorderLayout.EAST);

        // Wire button actions
        chooseImageButton.addActionListener(e -> handleChooseImage());
        compressButton.addActionListener(e -> handleCompression());

        return topButtonsPanel;
    }

    /**
     * Creates the bottom panel with side-by-side image preview boxes.
     *
     * @return configured images panel
     */
    private JPanel createImagesPanel() {
        JPanel imagesPanel = getStyledPanel(PanelContrast.MEDIUM);
        imagesPanel.setLayout(new GridLayout(1, 2, 30, 0));
        imagesPanel.setBorder(new EmptyBorder(20, 25, 20, 25));

        originalBox = createImageBox(GUIConstants.LABEL_ORIGINAL);
        compressedBox = createImageBox(GUIConstants.LABEL_COMPRESSED);

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
        log.debug(GUIConstants.LOG_OPENING_IMAGE_PICKER);
        ImagePicker imagePicker = new ImagePicker();

        imagePicker.subscribe(pair -> {
            selectedImageName = extractFilename(pair.getFirst());
            selectedImage = ImageUtils.copyBufferedImage(pair.getSecond());

            log.info(String.format(GUIConstants.LOG_IMAGE_SELECTED,
                    selectedImageName, selectedImage.getWidth(), selectedImage.getHeight()));

            showImage(originalBox, selectedImage, selectedImageName);
        });

        imagePicker.showUI();
    }

    /**
     * Handles the "Compress Image" button action.
     * <p>
     * If an image is selected, creates an {@link CompressionCoefficientsPicker} and waits for compression
     * parameters. Once received, compresses a copy of the original and displays the result.
     * </p>
     */
    private void handleCompression() {
        if (selectedImage == null) {
            log.warn(GUIConstants.LOG_COMPRESS_WITHOUT_IMAGE);
            return;
        }

        log.debug(GUIConstants.LOG_OPENING_PARAMETERS_PICKER);
        CompressionCoefficientsPicker integerPicker = new CompressionCoefficientsPicker();

        integerPicker.subscribe(pair -> {
            int F = pair.getFirst();
            int d = pair.getSecond();

            log.info(String.format(GUIConstants.LOG_COMPRESSION_START, F, d));

            // Make a defensive copy so we don't mutate the original image
            BufferedImage selectedCopy = ImageUtils.copyBufferedImage(selectedImage);

            new SwingWorker<BufferedImage, Void>() {

                @Override
                protected BufferedImage doInBackground() {
                    return new Part2().compress(
                            new Pair<>(
                                    selectedImageName + GUIConstants.COMPRESSED_SUFFIX,
                                    selectedCopy
                            ),
                            F,
                            d
                    );
                }

                @Override
                protected void done() {
                    try {
                        BufferedImage compressed = get();

                        log.info(String.format(
                                GUIConstants.LOG_COMPRESSION_DONE,
                                selectedImageName + GUIConstants.COMPRESSED_SUFFIX,
                                compressed.getWidth(),
                                compressed.getHeight()
                        ));

                        showImage(
                                compressedBox,
                                compressed,
                                selectedImageName + GUIConstants.COMPRESSED_SUFFIX
                        );

                    } catch (Exception e) {
                        log.error(
                                GUIConstants.LOG_COMPRESSION_FAILED_PREFIX + e.getMessage(),
                                e
                        );
                    }
                }
            }.execute();
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

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(0, 15));
        panel.setBackground(new Color(45, 45, 45));
        panel.setBorder(new LineBorder(new Color(70, 130, 180), 2));

        // Title label with better styling
        JLabel titleLabel = getStyledHeadingLabel(title);
        titleLabel.setFont(new Font(GUIConstants.FONT_ARIAL, Font.BOLD, 22));

        JPanel titleContainer = new JPanel();
        titleContainer.setBackground(new Color(30, 30, 30));
        titleContainer.setBorder(new EmptyBorder(10, 15, 10, 15));
        titleContainer.add(titleLabel);

        panel.add(titleContainer, BorderLayout.NORTH);

        JLabel placeholderLabel = new JLabel(title, SwingConstants.CENTER);
        placeholderLabel.setFont(new Font(GUIConstants.FONT_ARIAL, Font.BOLD, 28));
        placeholderLabel.setForeground(new Color(120, 120, 120));

        panel.add(placeholderLabel, BorderLayout.CENTER);

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
        File file = new File(GUIConstants.OUTPUT_DIR_NAME + File.separator + name + GUIConstants.FILE_EXTENSION_BMP);
        double kb = ImageUtils.fileSizeInKb(file);

        return String.format(
                GUIConstants.IMAGE_METADATA_TEMPLATE,
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
        JPanel container = new JPanel(new BorderLayout(0, 12));
        container.setBackground(new Color(45, 45, 45));
        container.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Title at top
        JLabel titleLabel = getStyledLabel(name, SwingConstants.CENTER);
        titleLabel.setFont(new Font(GUIConstants.FONT_ARIAL, Font.BOLD, 20));
        titleLabel.setForeground(new Color(70, 130, 180));

        // Image in center with white background container
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setBackground(Color.WHITE);
        imagePanel.setBorder(new LineBorder(new Color(100, 100, 100), 1));
        imagePanel.add(new JLabel(new ImageIcon(scaled), SwingConstants.CENTER), BorderLayout.CENTER);

        // Size info at bottom
        JLabel sizeLabel = getStyledLabel(sizeText, SwingConstants.CENTER);
        sizeLabel.setFont(new Font(GUIConstants.FONT_ARIAL, Font.PLAIN, 14));
        sizeLabel.setForeground(new Color(150, 150, 150));

        container.add(titleLabel, BorderLayout.NORTH);
        container.add(imagePanel, BorderLayout.CENTER);
        container.add(sizeLabel, BorderLayout.SOUTH);

        return container;
    }
}