package com.example.GUI.UI;
import com.example.GUI.UI.ScalableImagePanel;
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
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutionException;
public class ImageViewerPanel extends JPanel {

    private final JLabel titleLabel;
    private final JLabel infoLabel;
    private final ScalableImagePanel imagePanel;

    public ImageViewerPanel(String title) {

        setLayout(new BorderLayout());
        setBackground(Color.DARK_GRAY);

        titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(
                new Font("Arial", Font.BOLD, 18)
        );

        infoLabel = new JLabel(" ", SwingConstants.CENTER);
        infoLabel.setForeground(Color.LIGHT_GRAY);
        infoLabel.setFont(
                new Font("Arial", Font.PLAIN, 12)
        );

        imagePanel = new ScalableImagePanel();

        // IMPORTANT
        imagePanel.setLayout(new BorderLayout());

        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(Color.DARK_GRAY);

        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        header.add(titleLabel);
        header.add(infoLabel);

        add(header, BorderLayout.NORTH);

        JPanel centerWrapper = new JPanel(new BorderLayout());
        centerWrapper.setBackground(Color.DARK_GRAY);

        centerWrapper.add(imagePanel, BorderLayout.CENTER);

        add(centerWrapper, BorderLayout.CENTER);
    }


    public void setImage(
            BufferedImage img,
            String name,
            double sizeKb
    ) {

        imagePanel.setImage(img);

        titleLabel.setText(name);

        infoLabel.setText(
                img.getWidth()
                        + "x"
                        + img.getHeight()
                        + " | "
                        + String.format("%.2f KB", sizeKb)
        );

        revalidate();
        repaint();
    }


    public ScalableImagePanel getImagePanel() {
        return imagePanel;
    }
}