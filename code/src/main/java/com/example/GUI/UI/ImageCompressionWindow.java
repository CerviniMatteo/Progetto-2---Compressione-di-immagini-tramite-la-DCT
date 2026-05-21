package com.example.GUI.UI;

import com.example.GUI.constants.GUIConstants;
import com.example.GUI.enums.ButtonStyle;
import com.example.GUI.enums.PanelContrast;
import com.example.assignment.launcher.PartsLauncher;
import com.example.utils.ImageUtils;
import org.apache.commons.math3.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

import static com.example.GUI.constants.UIStyleConstants.*;
import static com.example.GUI.factory.StylingFactory.*;

public class ImageCompressionWindow extends JFrame {

    private static final Logger log =
            LogManager.getLogger(ImageCompressionWindow.class);

    private BufferedImage selectedImage;
    private String selectedImageName;
    private File selectedImageFile;

    private ImageViewerPanel originalViewer;
    private ImageViewerPanel compressedViewer;

    public ImageCompressionWindow() {

        super(GUIConstants.APP_TITLE);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(createTopPanel(), BorderLayout.NORTH);
        add(createImagesPanel(), BorderLayout.CENTER);

        setVisible(true);
    }

    private JPanel createTopPanel() {
        JPanel topButtonsPanel = getStyledPanel(PanelContrast.HIGH);
        topButtonsPanel.setLayout(new BorderLayout(GAP_HORIZONTAL_STANDARD, GAP_VERTICAL_STANDARD));
        topButtonsPanel.setBorder(new EmptyBorder(BORDER_TOP_TOP_CONTROLS, BORDER_LEFT_TOP_CONTROLS, BORDER_BOTTOM_TOP_CONTROLS, BORDER_RIGHT_TOP_CONTROLS));
        JLabel titleLabel = getStyledHeadingLabel(GUIConstants.DCT_IMAGE_COMPRESSION_TITLE);
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setBackground(COLOR_DARK);
        buttonsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, GAP_HORIZONTAL_STANDARD, 0));
        JButton chooseImageButton = getStyledButton(GUIConstants.BUTTON_CHOOSE_IMAGE, ButtonStyle.STYLE2);
        JButton compressButton = getStyledButton(GUIConstants.BUTTON_COMPRESS_IMAGE, ButtonStyle.STYLE3);
        buttonsPanel.add(chooseImageButton);
        buttonsPanel.add(compressButton);
        topButtonsPanel.add(titleLabel, BorderLayout.WEST);
        topButtonsPanel.add(buttonsPanel, BorderLayout.EAST);
        chooseImageButton.addActionListener(e -> handleChooseImage());
        compressButton.addActionListener(e -> handleCompression());
        return topButtonsPanel;
    }

    private JPanel createImagesPanel() {

        JPanel imagesPanel = getStyledPanel(PanelContrast.MEDIUM);

        imagesPanel.setLayout(new GridBagLayout());

        imagesPanel.setBorder(
                new EmptyBorder(
                        BORDER_TOP_PANEL,
                        BORDER_LEFT_PANEL,
                        BORDER_BOTTOM_PANEL,
                        BORDER_RIGHT_PANEL
                )
        );

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        gbc.weightx = 0.5;

        // LEFT: original image
        gbc.gridx = 0;
        gbc.gridy = 0;

        originalViewer = new ImageViewerPanel(GUIConstants.LABEL_ORIGINAL);
        imagesPanel.add(originalViewer, gbc);

        // RIGHT: compressed image
        gbc.gridx = 1;
        gbc.gridy = 0;

        compressedViewer = new ImageViewerPanel(GUIConstants.LABEL_COMPRESSED);
        imagesPanel.add(compressedViewer, gbc);

        return imagesPanel;
    }

    private void handleChooseImage() {

        ImagePicker imagePicker = new ImagePicker();

        /**
         * IMPORTANT: now expecting Pair<File, BufferedImage>
         */
        imagePicker.subscribe(pair -> {

            File file = pair.getFirst();
            BufferedImage img = pair.getSecond();

            new SwingWorker<BufferedImage, Void>() {

                @Override
                protected BufferedImage doInBackground() {
                    return ImageUtils.copyBufferedImage(img);
                }

                @Override
                protected void done() {
                    try {

                        selectedImageFile = file;
                        selectedImageName = file.getName();

                        double sizeKb = file.length() / 1024.0;

                        originalViewer.setImage(
                                selectedImage,
                                selectedImageName,
                                sizeKb
                        );

                    } catch (Exception e) {
                        log.error("Error loading image", e);
                    }
                }

            }.execute();
        });

        imagePicker.showUI();
    }

    private void handleCompression() {

        if (selectedImage == null) {
            log.warn(GUIConstants.LOG_COMPRESS_WITHOUT_IMAGE);
            return;
        }

        CompressionCoefficientsPicker picker =
                new CompressionCoefficientsPicker(
                        selectedImage.getWidth(),
                        selectedImage.getHeight()
                );

        picker.subscribe(pair -> {

            int F = pair.getFirst();
            int d = pair.getSecond();

            String outputName =
                    selectedImageName + GUIConstants.COMPRESSED_SUFFIX;

            new SwingWorker<BufferedImage, Void>() {

                @Override
                protected BufferedImage doInBackground() {

                    return PartsLauncher
                            .getInstance()
                            .launchPart2(
                                    F,
                                    d,
                                    new Pair<>(
                                            outputName,
                                            ImageUtils.copyBufferedImage(selectedImage)
                                    )
                            );
                }

                @Override
                protected void done() {

                    try {

                        BufferedImage compressed = get();

                        File outFile = new File(
                                GUIConstants.OUTPUT_DIR_NAME
                                        + File.separator
                                        + outputName
                                        + GUIConstants.FILE_EXTENSION_BMP
                        );

                        double sizeKb = outFile.length() / 1024.0;

                        compressedViewer.setImage(
                                compressed,
                                outputName,
                                sizeKb
                        );

                    } catch (Exception e) {
                        log.error(
                                GUIConstants.LOG_COMPRESSION_FAILED_PREFIX,
                                e.getMessage(),
                                e
                        );
                    }
                }

            }.execute();
        });

        picker.showUI();
    }
}