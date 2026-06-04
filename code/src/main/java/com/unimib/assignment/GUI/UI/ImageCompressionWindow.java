package com.unimib.assignment.GUI.UI;

import com.unimib.assignment.GUI.constants.GUIConstants;
import com.unimib.assignment.GUI.enums.ButtonStyle;
import com.unimib.assignment.GUI.enums.PanelContrast;
import com.unimib.assignment.backend.launcher.PartsLauncher;
import com.unimib.assignment.GUI.utils.ImageUtils;
import com.unimib.assignment.GUI.utils.SwingWorkerHelper;
import org.apache.commons.math3.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

import static com.unimib.assignment.GUI.constants.UIStyleConstants.*;
import static com.unimib.assignment.GUI.factory.StylingFactory.*;

public class ImageCompressionWindow extends JFrame {

    private static final Logger log = LogManager.getLogger(ImageCompressionWindow.class);


    private BufferedImage selectedImage;
    private String selectedImageName;

    private ImageViewerPanel originalViewer;
    private ImageViewerPanel compressedViewer;

    public ImageCompressionWindow() {

        super(GUIConstants.APP_TITLE);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(scale(960), scale(540)));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(createTopPanel(), BorderLayout.NORTH);
        add(createImagesPanel(), BorderLayout.CENTER);

        setVisible(true);
    }

    private JPanel createTopPanel() {
        JPanel topButtonsPanel = getStyledPanel(PanelContrast.HIGH);
        topButtonsPanel.setLayout(new BorderLayout(scale(GAP_HORIZONTAL_STANDARD), scale(GAP_VERTICAL_STANDARD)));
        topButtonsPanel.setBorder(new EmptyBorder(
                scale(BORDER_TOP_TOP_CONTROLS),
                scale(BORDER_LEFT_TOP_CONTROLS),
                scale(BORDER_BOTTOM_TOP_CONTROLS),
                scale(BORDER_RIGHT_TOP_CONTROLS)
        ));
        JLabel titleLabel = getStyledHeadingLabel(GUIConstants.DCT_IMAGE_COMPRESSION_TITLE);
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setBackground(COLOR_DARK);
        buttonsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, scale(GAP_HORIZONTAL_STANDARD), 0));
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
                        scale(BORDER_TOP_PANEL),
                        scale(BORDER_LEFT_PANEL),
                        scale(BORDER_BOTTOM_PANEL),
                        scale(BORDER_RIGHT_PANEL)
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

        imagePicker.subscribe(pair -> {

            File file = pair.getFirst();
            BufferedImage img = pair.getSecond();

            new SwingWorkerHelper<>(() -> ImageUtils.copyBufferedImage(img))
                .onSuccess(copiedImage -> {
                    selectedImageName = file.getName();
                    selectedImage = copiedImage;
                    double sizeKb = file.length() / 1024.0;

                    originalViewer.setImage(
                            selectedImage,
                            selectedImageName.substring(0, selectedImageName.lastIndexOf(".")),
                            sizeKb
                    );
                })
                .onError(e -> log.error("Error loading image", e))
                .execute();
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
                    selectedImageName.substring(0, selectedImageName.lastIndexOf(".")) + GUIConstants.COMPRESSED_SUFFIX;

            new SwingWorkerHelper<>(() ->
                PartsLauncher
                    .getInstance()
                    .launchPart2(
                        F,
                        d,
                        new Pair<>(
                            outputName,
                            ImageUtils.copyBufferedImage(selectedImage)
                        )
                    )
            )
            .onSuccess(compressed -> {
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
            })
            .onError(e -> log.error(
                    GUIConstants.LOG_COMPRESSION_FAILED_PREFIX,
                    e.getMessage(),
                    e
            ))
            .execute();
        });

        picker.showUI();
    }
}