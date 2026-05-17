package com.example.GUI.UI;

import com.example.GUI.enums.ButtonStyle;
import com.example.GUI.enums.PanelContrast;
import com.example.GUI.utils.ImagePreviewRenderer;
import com.example.assignment.Part2;
import com.example.GUI.constants.GUIConstants;
import com.example.assignment.launcher.PartLauncher;
import com.example.lib.utils.ImageUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.math3.util.Pair;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutionException;

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

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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
            String imageName = extractFilename(pair.getFirst());
            new SwingWorker<BufferedImage, Void>() {

                @Override
                protected BufferedImage doInBackground() {
                    return ImageUtils.copyBufferedImage(pair.getSecond());
                }

                @Override
                protected void done() {
                    try {
                        selectedImageName = imageName;
                        selectedImage = get();

                        log.info(String.format(GUIConstants.LOG_IMAGE_SELECTED,
                                selectedImageName, selectedImage.getWidth(), selectedImage.getHeight()));

                        ImagePreviewRenderer.getInstance().showImageAsync(originalBox, selectedImage, selectedImageName, log);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        log.error(GUIConstants.LOG_COMPRESSION_FAILED_PREFIX + e.getMessage(), e);
                    } catch (ExecutionException e) {
                        Throwable cause = e.getCause() != null ? e.getCause() : e;
                        log.error(GUIConstants.LOG_COMPRESSION_FAILED_PREFIX + cause.getMessage(), cause);
                    }
                }
            }.execute();
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
        CompressionCoefficientsPicker integerPicker = new CompressionCoefficientsPicker(selectedImage.getWidth(), selectedImage.getHeight());

        integerPicker.subscribe(pair -> {
            int F = pair.getFirst();
            int d = pair.getSecond();

            log.info(String.format(GUIConstants.LOG_COMPRESSION_START, F, d));
            BufferedImage sourceImage = selectedImage;
            String outputName = selectedImageName + GUIConstants.COMPRESSED_SUFFIX;

            new SwingWorker<BufferedImage, Void>() {

                @Override
                protected BufferedImage doInBackground() {
                    BufferedImage selectedCopy = ImageUtils.copyBufferedImage(sourceImage);
                    return PartLauncher.getInstance().launchPart2(F, d, new Pair<>(outputName, selectedCopy));
                }

                @Override
                protected void done() {
                    try {
                        BufferedImage compressed = get();

                        log.info(String.format(
                                GUIConstants.LOG_COMPRESSION_DONE,
                                outputName,
                                compressed.getWidth(),
                                compressed.getHeight()
                        ));

                        ImagePreviewRenderer.getInstance().showImageAsync(
                                compressedBox,
                                compressed,
                                outputName,
                                log
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

}