package com.example.GUI.UI;

import com.example.GUI.constants.GUIConstants;
import com.example.GUI.model.PreviewData;
import com.example.lib.utils.ImageUtils;
import org.apache.commons.logging.Log;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.ExecutionException;

import static com.example.GUI.factory.StylingFactory.getStyledLabel;

/**
 * Utility responsible for rendering image previews in Swing panels.
 * <p>
 * The heavy work is performed in a {@link SwingWorker} so image conversion,
 * scaling, and metadata lookup do not block the Event Dispatch Thread.
 * </p>
 */
public final class ImagePreviewRenderer {

    private ImagePreviewRenderer() {
        // Utility class
    }

    /**
     * Precomputes and renders an image preview asynchronously.
     *
     * @param box target panel where the preview is rendered
     * @param image source image to preview
     * @param name base image name used for label and output file lookup
     * @param log logger used to report preview failures
     */
    public static void showImageAsync(JPanel box, BufferedImage image, String name, Log log) {
        int boxW = Math.max(box.getWidth() - 40, 100);
        int boxH = Math.max(box.getHeight() - 80, 80);

        new SwingWorker<PreviewData, Void>() {
            @Override
            protected PreviewData doInBackground() {
                BufferedImage rgb = ImageUtils.toRgbImage(image);
                String sizeText = formatImageMetadata(image, name);
                Image scaled = ImageUtils.scaleImageToFit(rgb, boxW, boxH);
                return new PreviewData(scaled, sizeText);
            }

            @Override
            protected void done() {
                try {
                    PreviewData previewData = get();
                    JPanel container = createImageLabel(name, previewData.scaled(), previewData.sizeText());

                    box.removeAll();
                    box.add(container, BorderLayout.CENTER);
                    box.revalidate();
                    box.repaint();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.error(GUIConstants.LOG_PREVIEW_FAILED_PREFIX + e.getMessage(), e);
                } catch (ExecutionException e) {
                    Throwable cause = e.getCause() != null ? e.getCause() : e;
                    log.error(GUIConstants.LOG_PREVIEW_FAILED_PREFIX + cause.getMessage(), cause);
                }
            }
        }.execute();
    }

    /**
     * Formats image metadata (dimensions and file size) as HTML.
     *
     * @param image the image to inspect
     * @param name output filename (used to look up the saved BMP)
     * @return HTML-formatted metadata string
     */
    private static String formatImageMetadata(BufferedImage image, String name) {
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

        JLabel titleLabel = getStyledLabel(name, SwingConstants.CENTER);
        titleLabel.setFont(new Font(GUIConstants.FONT_ARIAL, Font.BOLD, 20));
        titleLabel.setForeground(new Color(70, 130, 180));

        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setBackground(Color.WHITE);
        imagePanel.setBorder(new LineBorder(new Color(100, 100, 100), 1));
        imagePanel.add(new JLabel(new ImageIcon(scaled), SwingConstants.CENTER), BorderLayout.CENTER);

        JLabel sizeLabel = getStyledLabel(sizeText, SwingConstants.CENTER);
        sizeLabel.setFont(new Font(GUIConstants.FONT_ARIAL, Font.PLAIN, 14));
        sizeLabel.setForeground(new Color(150, 150, 150));

        container.add(titleLabel, BorderLayout.NORTH);
        container.add(imagePanel, BorderLayout.CENTER);
        container.add(sizeLabel, BorderLayout.SOUTH);

        return container;
    }
}

