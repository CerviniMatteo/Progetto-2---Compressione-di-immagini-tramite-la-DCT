package com.example.GUI.utils;

import com.example.GUI.constants.GUIConstants;
import com.example.GUI.model.PreviewData;
import com.example.lib.utils.ImageUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.ExecutionException;

import static com.example.GUI.factory.StylingFactory.getStyledLabel;
import static com.example.GUI.constants.UIStyleConstants.*;

/**
 * Singleton utility responsible for rendering image previews in Swing panels.
 * <p>
 * The heavy work is performed in a {@link SwingWorker} so image conversion,
 * scaling, and metadata lookup do not block the Event Dispatch Thread.
 * </p>
 */
public final class ImagePreviewRenderer {

    private static volatile ImagePreviewRenderer INSTANCE;
    private static final Logger log = LogManager.getLogger(ImagePreviewRenderer.class);

    private ImagePreviewRenderer() {
    }

    public static ImagePreviewRenderer getInstance() {
        if (INSTANCE == null) {
            synchronized (ImagePreviewRenderer.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ImagePreviewRenderer();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Precomputes and renders an image preview asynchronously.
     *
     * @param box target panel where the preview is rendered
     * @param image source image to preview
     * @param name base image name used for label and output file lookup
     */
      public void showImageAsync(JPanel box, BufferedImage image, String name) {
         int boxW = Math.max(box.getWidth() - IMAGE_PREVIEW_PADDING, MIN_IMAGE_DIMENSION);
         int boxH = Math.max(box.getHeight() - IMAGE_PREVIEW_TITLE_HEIGHT, MIN_IMAGE_DIMENSION);

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
                    log.error(GUIConstants.LOG_PREVIEW_FAILED_PREFIX, e.getMessage(), e);
                } catch (ExecutionException e) {
                    Throwable cause = e.getCause() != null ? e.getCause() : e;
                    log.error(GUIConstants.LOG_PREVIEW_FAILED_PREFIX, cause.getMessage(), cause);
                }
            }
        }.execute();
    }

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

     private static JPanel createImageLabel(String name, Image scaled, String sizeText) {
         JPanel container = new JPanel(new BorderLayout(0, GAP_VERTICAL_PREVIEW));
         container.setBackground(COLOR_MEDIUM_DARK);
         container.setBorder(new EmptyBorder(BORDER_TOP_PREVIEW, BORDER_LEFT_PREVIEW, BORDER_BOTTOM_PREVIEW, BORDER_RIGHT_PREVIEW));

         JLabel titleLabel = getStyledLabel(name, SwingConstants.CENTER);
         titleLabel.setFont(new Font(GUIConstants.FONT_ARIAL, Font.BOLD, FONT_SIZE_PREVIEW_LABEL));
         titleLabel.setForeground(COLOR_STEELBLUE);

         JPanel imagePanel = new JPanel(new BorderLayout());
         imagePanel.setBackground(Color.WHITE);
         imagePanel.setBorder(new LineBorder(COLOR_GRAY_BORDER, BORDER_WIDTH_STANDARD));
         imagePanel.add(new JLabel(new ImageIcon(scaled), SwingConstants.CENTER), BorderLayout.CENTER);

         JLabel sizeLabel = getStyledLabel(sizeText, SwingConstants.CENTER);
         sizeLabel.setFont(new Font(GUIConstants.FONT_ARIAL, Font.PLAIN, FONT_SIZE_SMALL));
         sizeLabel.setForeground(COLOR_GRAY_PLACEHOLDER);

         container.add(titleLabel, BorderLayout.NORTH);
         container.add(imagePanel, BorderLayout.CENTER);
         container.add(sizeLabel, BorderLayout.SOUTH);

         return container;
     }
}

