package com.unimib.assignment.GUI.UI;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * JPanel capable of rendering a BufferedImage that automatically
 * rescales when the window is resized.
 */
public class ScalableImagePanel extends JPanel {

    private BufferedImage image;

    private double zoom = 1.0; // user zoom factor
    public ScalableImagePanel() {

        setBackground(Color.DARK_GRAY);
        // mouse wheel zoom
        addMouseWheelListener(e -> {
            double delta = -e.getPreciseWheelRotation() * 0.1;
            zoom += delta;

            // clamp zoom
            zoom = Math.max(0.1, Math.min(zoom, 10.0));

            repaint();
        });
    }

    /**
     * Sets the image to display.
     *
     * @param image image to render
     */
    public void setImage(BufferedImage image) {
        this.image = image;
        this.zoom = 1.0; // reset zoom when new image is loaded
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        if (image == null) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g.create();

        // IMPORTANT: pixel-perfect scaling
        g2.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR
        );

        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_OFF
        );

        int panelW = getWidth();
        int panelH = getHeight();

        int imgW = image.getWidth();
        int imgH = image.getHeight();

        double fitScale = Math.min(
                (double) panelW / imgW,
                (double) panelH / imgH
        );

        double scale = fitScale * zoom;

        int drawW = (int) (imgW * scale);
        int drawH = (int) (imgH * scale);

        int x = (panelW - drawW) / 2;
        int y = (panelH - drawH) / 2;

        g2.drawImage(image, x, y, drawW, drawH, null);

        g2.dispose();
    }
}